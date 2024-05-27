use diffeq_solver::{
    diffeq::DifferentialEquation,
    function::Function2Variables,
    misc::get_table_str,
    solve::{DiffEquationSolution, DiffEquationSolver, RegularDiffEquationOptions, Solvers},
};
use eframe::egui;
use egui::Color32;
use egui_plot::{Legend, Line, Plot, PlotItem, PlotPoints, Points};
use std::{error::Error, io, rc::Rc, str::FromStr, sync::mpsc, thread};
use std::{f64::consts::E, fmt::Write};

fn main() -> Result<(), Box<dyn Error>> {
    let (sender, receiver) = mpsc::channel();
    let options = eframe::NativeOptions {
        viewport: egui::ViewportBuilder::default().with_inner_size([800.0, 800.0]),
        run_and_return: true,
        ..Default::default()
    };
    let _ = eframe::run_native(
        "Differential Equation Solver 4000",
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
        let basic_polynomial_func =
            Function2Variables::new(|x, y| y + (1.0 + x) * y * y, "y+(1+x)y^2");
        let basic_polynomial_solution = Function2Variables::new(
            |x, constant| -(x.exp() / (constant + x.exp() * x)),
            "-(e^x)/(c_1 + e^x * x)",
        );
        let basic_polynomial_constant_calc =
            |initial: (f64, f64)| -(initial.0.exp() / initial.1) - initial.0.exp() * initial.0;

        let primitive_polynomial_func = Function2Variables::new(|x, y| x * x + x, "x^2+x");
        let primitive_polynomial_solution = Function2Variables::new(
            |x, constant| constant + x.powi(3) / 3.0 + x.powi(2) / 2.0,
            "c_1 + x^3/3 + x^2/2",
        );
        let primitive_polynomial_constant_calc =
            |initial: (f64, f64)|   initial.1 - initial.0.powi(3) / 3.0 - initial.0.powi(2) / 2.0;

            let trig_func = Function2Variables::new(|x, y| y + x * x.sin(), "y + x*sin(x)");
            let trig_solution = Function2Variables::new(
                |x, constant| constant * x.exp() - 0.5 * x * x.sin() - 0.5 * x * x.cos() - 0.5 * x.cos(),
                "c_1*e^x-0.5x*sin(x) - 0.5x*cos(x)-cos(x)/2",
            );
            let trig_constant_calc =
                |i: (f64, f64)|   (i.1 + 0.5 * i.0 * i.0.sin() + 0.5 * i.0 * i.0.cos() + i.0.cos()/2.0)/i.0.exp();
    

        let equations_pool = vec![
            DifferentialEquation::new(
                basic_polynomial_func,
                basic_polynomial_solution,
                basic_polynomial_constant_calc,
            ),
            DifferentialEquation::new(
                primitive_polynomial_func,
                primitive_polynomial_solution,
                primitive_polynomial_constant_calc,
            ),
            DifferentialEquation::new(
                trig_func,
                trig_solution,
                trig_constant_calc,
            ),
        ];

        let options = input_options(&equations_pool);

        let euler_solution = Solvers::Euler.solve(&options);
        let modeuler_solution = Solvers::ModifiedEuler.solve(&options);
        let milne_solution = Solvers::Milne.solve(&options);
        let mut solutions = vec![euler_solution, modeuler_solution, milne_solution];

        let mut success_solutions: Vec<Box<dyn DiffEquationSolution>> =
            solutions.into_iter().filter_map(|x| x.ok()).collect();

        println!("\nCalculated Solutions\n");
        for sol in &success_solutions {
            println!("{}", sol);
        }
        sender.send(AppMessage {
            solutions: success_solutions,
            equation_tuple: Some((options.equation().clone(), options)),
        })?;
        ctx.request_repaint();
    }
}

struct AppMessage {
    solutions: Vec<Box<dyn DiffEquationSolution>>,
    equation_tuple: Option<(DifferentialEquation, RegularDiffEquationOptions)>,
}
struct App {
    rx: mpsc::Receiver<AppMessage>,
    solutions: Vec<Box<dyn DiffEquationSolution>>,
    equation_tuple: Option<(DifferentialEquation, RegularDiffEquationOptions)>,
}
impl App {
    pub fn new(rx: mpsc::Receiver<AppMessage>) -> App {
        return App {
            rx,
            solutions: vec![],
            equation_tuple: None,
        };
    }
}

const GRUVBOX_PLOT_COLORS: [Color32; 5] = [
    Color32::from_rgb(204, 36, 29),
    Color32::from_rgb(152, 152, 26),
    Color32::from_rgb(215, 153, 33),
    Color32::from_rgb(69, 133, 136),
    Color32::from_rgb(177, 98, 134),
];

impl eframe::App for App {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        if let Ok(msg) = self.rx.try_recv() {
            self.solutions = msg.solutions;
            self.equation_tuple = msg.equation_tuple;
        }
        let solutions_clones = self.solutions.clone();
        egui::CentralPanel::default().show(ctx, |ui| {
            let plot = Plot::new("Equation Plot").legend(Legend::default());
            let inner = plot.show(ui, |plot_ui| {
                for (i, solution) in solutions_clones.iter().enumerate() {
                    let line_name = format!("{}", solution.display_name());
                    let points: Vec<[f64; 2]> = solution
                        .solution()
                        .into_iter()
                        .map(|el| [el.0, el.1])
                        .collect();
                    let color = GRUVBOX_PLOT_COLORS[(i + 1) % GRUVBOX_PLOT_COLORS.len()];
                    plot_ui.points(
                        Points::new(PlotPoints::new(points.clone()))
                            .radius(5.0)
                            .name(line_name.clone())
                            .color(color),
                    );
                    plot_ui.line(
                        Line::new(PlotPoints::new(
                            solution
                                .solution()
                                .into_iter()
                                .map(|el| [el.0, el.1])
                                .collect(),
                        ))
                        .color(color)
                        .width(2.5)
                        .name(line_name),
                    );
                }
                if let Some((equation, opts)) = &self.equation_tuple {
                    let line_name = format!("{}", equation.solution());
                    let constant = equation.constant(opts.initial());
                    let equation_clone = equation.clone();
                    plot_ui.line(
                        Line::new(PlotPoints::from_explicit_callback(
                            move |x| equation_clone.solution().fun(x, constant),
                            ..,
                            200,
                        ))
                        .width(2.5)
                        .name(line_name)
                        .color(GRUVBOX_PLOT_COLORS[0]),
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

fn input_options(eq_pool: &Vec<DifferentialEquation>) -> RegularDiffEquationOptions {
    let mut inv_phrase = String::new();
    write!(
        inv_phrase,
        "Please, choose one of the following equation to solve:\n"
    )
    .expect("Writing to string.");
    for (i, &ref eq) in eq_pool.iter().enumerate() {
        write!(inv_phrase, "{}|y'={} \n", i + 1, eq.func()).expect("Writing to string.");
    }
    loop {
        let index: usize = input_until_parsed::<usize>(&inv_phrase, "Please provide an integer!");
        if index >= 1 && index <= eq_pool.len() {
            let mut a;
            let mut b;
            loop {
                a = input_until_parsed::<f64>(
                    "Please enter left (a) bound of interval:",
                    "Failed to parse your input!",
                );
                b = input_until_parsed::<f64>(
                    "Please enter right (b) bound of interval:",
                    "Failed to parse your input!",
                );
                if a >= b {
                    println!(
                        "Invalid interval provided - please, choose left limit smaller than the right one"
                    );
                } else {
                    break;
                }
            }

            let initial_y = loop {
                let input = input_until_parsed::<f64>(
                    &format!("Please provide initial y_0 at x_0={}:", a),
                    "Initial y_0 be a valid f64.",
                );
                break input;
            };

            let h = loop {
                let input = input_until_parsed::<f64>(
                    "Please provide initial step size(h):",
                    "Initial step size must be a valid f64.",
                );
                if input <= 0.0 {
                    println!("This must be a positive number!");
                    continue;
                }
                break input;
            };

            let precision = loop {
                let input = input_until_parsed::<f64>(
                    "Please provide precision (eps):",
                    "Precision must be a valid f64.",
                );
                if input <= 0.0 {
                    println!("This must be a positive number!");
                    continue;
                }
                break input;
            };

            return RegularDiffEquationOptions::new(
                eq_pool[index - 1].clone(),
                (a, initial_y),
                b,
                h,
                precision,
            );
        } else {
            println!("Provided index doesn't correspond to any eqaution! Try again!");
        }
    }
}
