use eframe::egui;
use egui_plot::{Legend, Line, Plot, PlotPoints, Points};
use func_interpol::{
    function::Function,
    interpol::{
        CubicSplineInterpolation, FunctionInterpolation, InterpolationBuilder, LagrangeInterpolation, NewtonIrregularGridInterpolation, NewtonRegularGridInterpolation, RealFunction, RegularInterpolationOptions
    },
    misc::get_table_str,
};
use std::fmt::Write;
use std::{error::Error, io, rc::Rc, str::FromStr, sync::mpsc, thread};

fn main() -> Result<(), Box<dyn Error>> {
    let (sender, receiver) = mpsc::channel();
    let options = eframe::NativeOptions {
        viewport: egui::ViewportBuilder::default().with_inner_size([800.0, 800.0]),
        run_and_return: true,
        ..Default::default()
    };
    let _ = eframe::run_native(
        "Function Interpolator 4000",
        options,
        Box::new(move |cc| {
            let frame = cc.egui_ctx.clone();
            thread::spawn(move || {
                let _ = console_app(sender, frame);
                std::process::exit(1);
            });
            Box::new(App::new(receiver))
        }),
    );
    return Ok(());
}
fn console_app(sender: mpsc::Sender<AppMessage>, ctx: egui::Context) -> Result<(), Box<dyn Error>> {
    loop {
        let basic_polynomial_func = Function::new(|x| 1.0 + 2.0 * x - 3.0, "1+2x-3");
        let complex_polynomial_func = Function::new(
            |x| x.powi(3) + 4.81 * x.powi(2) - 17.37 * x + 5.38,
            "x^3 + 4.81x^2 - 17.37x + 5.38",
        );
        let transcendent_trig_func =
            Function::new(|x| 5.0 * x.sin() - 2.0 * x.cos(), "5*sin(x) - 2*cos(x)");

        let func_pool = vec![
            basic_polynomial_func,
            complex_polynomial_func,
            transcendent_trig_func,
        ];

        let interpolating_points = input_points(&func_pool);
        let points = match &interpolating_points {
            InterpolatingPoints::AsTable(points) => points.clone(),
            InterpolatingPoints::ASFunction(func, points) => points.clone(),
        };
        let points_as_arr = points.iter().map(|el| [el.0, el.1]).collect();
        let opts = RegularInterpolationOptions::new(points);

        match opts.finite_differences(){
            Some(diff) => {
                let mut diff_header = (1..diff.len())
                .map(|el| format!("d^{el} f_i"))
                .collect::<Vec<String>>();
                diff_header.insert(0, "f_i".to_owned());
                println!("Finite differences:\n{}\n", get_table_str(&diff_header, diff.iter().map(|el| el).collect(),8, 3));
            },
            None => println!("Provided coordinates doesn't form regular grid! Some interpolation methods are disabled!"),
        }

        let lagr_interpol = LagrangeInterpolation {}.approximate(&opts).unwrap();
        let newton_irreg_interpol = NewtonIrregularGridInterpolation {}
            .approximate(&opts)
            .unwrap();

        let spline_interpol = CubicSplineInterpolation {}.approximate(&opts).unwrap();

        let mut interpols = vec![spline_interpol, lagr_interpol, newton_irreg_interpol];

        match interpolating_points{
            InterpolatingPoints::AsTable(_) => (),
            InterpolatingPoints::ASFunction(func, _) => interpols.push(Box::new(RealFunction::new(func))),
        }

        if opts.is_regular_grid() {
            interpols.push(
                NewtonRegularGridInterpolation {}
                    .approximate(&opts)
                    .unwrap(),
            );
        }

        println!("\nCalculated Interpolations\n");
        for interpol in &interpols {
            println!("{}", interpol);
        }
        sender.send(AppMessage {
            interpolations: interpols,
            points: points_as_arr,
        })?;
        ctx.request_repaint();
    }
}

struct AppMessage {
    interpolations: Vec<Box<dyn FunctionInterpolation>>,
    points: Vec<[f64; 2]>,
}
struct App {
    rx: mpsc::Receiver<AppMessage>,
    interpolations: Vec<Box<dyn FunctionInterpolation>>,
    points: Vec<[f64; 2]>,
}
impl App {
    pub fn new(rx: mpsc::Receiver<AppMessage>) -> App {
        return App {
            rx,
            interpolations: vec![],
            points: vec![],
        };
    }
}

impl eframe::App for App {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        if let Ok(msg) = self.rx.try_recv() {
            self.interpolations = msg.interpolations;
            self.points = msg.points;
        }
        let interpols_clones = self.interpolations.clone();
        egui::CentralPanel::default().show(ctx, |ui| {
            let plot = Plot::new("Interpolation Plot").legend(Legend::default());
            let inner = plot.show(ui, |plot_ui| {
                plot_ui.points(
                    Points::new(PlotPoints::new(self.points.clone()))
                        .radius(5.0)
                        .name("Interpolated data"),
                );

                for interpol in interpols_clones {
                    let line_name = format!("{}", interpol.legend_name());
                    plot_ui.line(
                        Line::new(PlotPoints::from_explicit_callback(
                            move |x| interpol.fun(x),
                            ..,
                            200,
                        ))
                        .width(2.5)
                        .name(line_name),
                    );
                }
            });
        });
    }
}

fn input_until_parsed<T: FromStr>(phrase: &str, fail_phrase: &str) -> T {
    loop {
        let mut buf = String::new();
        println!("{}", phrase);
        io::stdin()
            .read_line(&mut buf)
            .expect("Failed to read from stdin. Sorry, I can't cope with that!");
        match (buf.trim()).parse::<T>() {
            Ok(val) => {
                return val;
            }
            Err(_err) => {
                println!("{}", fail_phrase);
            }
        }
    }
}

fn get_points_from_csv(csv_path: &str) -> Result<Vec<(f64, f64)>, Box<dyn Error>> {
    let mut reader = csv::Reader::from_path(csv_path)?;
    let points_record = reader.records();

    let mut points: Vec<(f64, f64)> = vec![];
    println!(
        "---------------------\n\
            Parsing provided points (x, y):\n\
            ---------------------\n"
    );
    for record in points_record {
        let str_rec = record?;
        let read_x = str_rec.get(0);
        let read_y = str_rec.get(1);

        match read_x {
            Some(x) => match read_y {
                Some(y) => {
                    println!("{} {}", x, y);
                    let x_parsed = x.trim().parse::<f64>();
                    if let Err(err) = x_parsed {
                        return Err(format!("Failed to parse x from csv because of: {err}").into());
                    }
                    let y_parsed = y.trim().parse::<f64>();
                    if let Err(err) = y_parsed {
                        return Err(format!("Failed to parse y from csv because of: {err}").into());
                    }
                    points.push((x_parsed?, y_parsed?));
                }
                None => {
                    return Err("Failed to read y in a provided csv row.".into());
                }
            },
            None => {
                return Err("Failed to read x in a provided csv row.".into());
            }
        }
    }
    return Ok(points);
}

enum InterpolatingPoints {
    AsTable(Vec<(f64, f64)>),
    ASFunction(Function, Vec<(f64, f64)>),
}

fn input_points(func_pool: &Vec<Function>) -> InterpolatingPoints {
    let mut inv_phrase = String::new();
    write!(
        inv_phrase,
        "Please, choose one of the following option to input points:\n"
    )
    .expect("Writing to string.");
    for (i, &ref func) in func_pool.iter().enumerate() {
        write!(inv_phrase, "{}| {} \n", i + 1, func).expect("Writing to string.");
    }
    write!(
        inv_phrase,
        "{}| {} \n",
        func_pool.len() + 1,
        "Via a csv file"
    )
    .expect("Writing to string.");
    write!(
        inv_phrase,
        "{}| {} \n",
        func_pool.len() + 2,
        "Point by point"
    )
    .expect("Writing to string.");
    loop {
        let index: usize = input_until_parsed::<usize>(&inv_phrase, "Please provide an integer!");
        if index >= 1 && index <= func_pool.len() {
            let mut a;
            let mut b;
            loop {
                a = input_until_parsed::<f64>(
                    "Please enter left (a) bound of interpolation:",
                    "Failed to parse your input!",
                );
                b = input_until_parsed::<f64>(
                    "Please enter right (b) bound of interpolation:",
                    "Failed to parse your input!",
                );
                if a > b {
                    println!(
                        "Invalid interval provided - please, choose left limit smaller than the right one"
                    );
                } else {
                    break;
                }
            }

            let n = loop {
                let input = input_until_parsed::<usize>(
                    "Please provide number of points to get from function:",
                    "Number of points must be a valid usize var.",
                );
                if input == 0 {
                    println!("This must be a positive number - not zero!");
                    continue;
                }
                break input;
            };

            let step = (b - a) / (n as f64);
            let mut points = vec![];
            while a <= b {
                points.push((a, func_pool[index - 1].fun(a)));
                a+=step;
            }

            return InterpolatingPoints::ASFunction(func_pool[index - 1].clone(), points);
        } else if index == func_pool.len() + 1 {
            let mut path;
            path = input_until_parsed::<String>(
                "Please provide a path to csv file to parse:",
                "A path must be a valid string",
            );
            match get_points_from_csv(&path) {
                Ok(points) => return InterpolatingPoints::AsTable(points),
                Err(err) => {
                    eprintln!("Failed to parse csv {path} because of: {err}");
                    continue;
                }
            }
        } else if index == func_pool.len() + 2 {
            let n = loop {
                let input = input_until_parsed::<usize>(
                    "Please provide number of points as integer:",
                    "Number of points must be a valid usize var.",
                );
                if input == 0 {
                    println!("This must be a positive number - not zero!");
                    continue;
                }
                break input;
            };

            let mut points = vec![(0.0, 0.0); n];
            for i in 0..n {
                let x = input_until_parsed::<f64>(
                    &format!("Please provide x coord of {i} point"),
                    "It must be a valid float value",
                );
                let y = input_until_parsed::<f64>(
                    &format!("Please provide y coord of {i} point"),
                    "It must be a valid float value",
                );

                points[i] = (x, y);
            }
            return InterpolatingPoints::AsTable(points);
        } else {
            println!("Provided index doesn't correspond to any function! Try again!");
        }
    }
}
