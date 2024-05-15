use std::{error::Error, fs::File, io, str::FromStr};

use eframe::egui;
use egui_plot::{Legend, Line, Plot, PlotPoints, Points};
use func_approx::approx::{
    ApproximationBuilder, ExponentApproximation, FunctionApproximation, LogarithmApproximation,
    PolynomialApproximation, PowerApproximation, RegularApproximationOptions,
};

fn main() -> Result<(), Box<dyn Error>> {
    loop {
        let mut by_file = true;
        loop {
            let by_file_yes_no = input_until_parsed::<String>(
                "Do you want to provide points by csv file? (Y,N):",
                "Answer must be a valid string",
            );
            match by_file_yes_no.to_lowercase().as_str() {
                "y" | "yes" => {
                    by_file = true;
                    break;
                }
                "n" | "no" => {
                    by_file = false;
                    break;
                }
                _ => {
                    println!("Please answer yes(y) or no(n).");
                    continue;
                }
            }
        }
        let points = match by_file {
            true => {
                let mut path;
                path = input_until_parsed::<String>(
                    "Please provide a path to csv file to parse:",
                    "A path must be a valid string",
                );
                match get_points_from_csv(&path) {
                    Ok(points) => points,
                    Err(err) => {
                        eprintln!("Failed to parse csv {path} because of: {err}");
                        continue;
                    }
                }
            }
            false => {
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
                points
            }
        };

        let points_as_arr = points.iter().map(|el| [el.0, el.1]).collect();
        let opts = RegularApproximationOptions::new(points);

        let lin_builder = PolynomialApproximation::new(1);
        let lin_approx = lin_builder.approximate(&opts).unwrap();

        let quad_builder = PolynomialApproximation::new(2);
        let quad_approx = quad_builder.approximate(&opts).unwrap();

        let cubic_builder = PolynomialApproximation::new(3);
        let cubic_approx = cubic_builder.approximate(&opts).unwrap();

        let exp_builder = ExponentApproximation {};
        let exp_approx = exp_builder.approximate(&opts).unwrap();

        let log_builder = LogarithmApproximation {};
        let log_approx = log_builder.approximate(&opts).unwrap();

        let pow_builder = PowerApproximation {};
        let pow_approx = pow_builder.approximate(&opts).unwrap();

        let approxs = vec![
            lin_approx,
            quad_approx,
            cubic_approx,
            exp_approx,
            log_approx,
            pow_approx,
        ];

        println!("\nCalculated approximations\n");

        let best_approx_by_dev = &approxs
            .iter()
            .filter(|el| el.sq_deviation().is_finite())
            .min_by(|a, b| a.sq_deviation().total_cmp(&b.sq_deviation()))
            .unwrap();
        let best_approx_by_determ = &approxs
            .iter()
            .filter(|el| el.sq_deviation().is_finite())
            .max_by(|a, b| a.determ_coeff().total_cmp(&b.determ_coeff()))
            .unwrap();
        for approx in &approxs {
            if approx.sq_deviation().is_finite() && approx.determ_coeff().is_finite() {
                println!("{}", approx);
            }
        }
        println!(
            "*******************\n\
        Best by smallest deviation ({}): {}\n\
        *******************",
            best_approx_by_dev.sq_deviation(),
            best_approx_by_dev.legend_name()
        );
        println!("Deviation vec: {:?}", best_approx_by_dev.deviaton_vec());
        println!("Approximation vec: {:?}", best_approx_by_dev.approx_vec());

        println!(
            "*******************\n\
        Best by highest determ. coeff ({}): {}\n\
        *******************",
            best_approx_by_determ.determ_coeff(),
            best_approx_by_determ.legend_name()
        );
        println!("Deviation vec: {:?}", best_approx_by_determ.deviaton_vec());
        println!(
            "Approximation vec: {:?}",
            best_approx_by_determ.approx_vec()
        );

        let options = eframe::NativeOptions {
            viewport: egui::ViewportBuilder::default().with_inner_size([800.0, 800.0]),
            run_and_return: true,
            ..Default::default()
        };

        let result = eframe::run_native(
            "Function Approximator 3000",
            options,
            Box::new(move |_cc| Box::new(App::new(approxs, points_as_arr))),
        );
    }
}

#[derive(Default)]
struct App {
    approximations: Vec<Box<dyn FunctionApproximation>>,
    points: Vec<[f64; 2]>,
}
impl App {
    pub fn new(approximations: Vec<Box<dyn FunctionApproximation>>, points: Vec<[f64; 2]>) -> App {
        return App {
            approximations,
            points,
        };
    }
}

impl eframe::App for App {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        let approx_clones = self.approximations.clone();
        egui::CentralPanel::default().show(ctx, |ui| {
            let plot = Plot::new("Approximation Plot").legend(Legend::default());
            let inner = plot.show(ui, |plot_ui| {
                plot_ui.points(
                    Points::new(PlotPoints::new(self.points.clone()))
                        .radius(5.0)
                        .name("Approximated data"),
                );

                for approx in approx_clones {
                    let line_name = format!("{}", approx.legend_name());
                    plot_ui.line(
                        Line::new(PlotPoints::from_explicit_callback(
                            move |x| approx.fun(x),
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
