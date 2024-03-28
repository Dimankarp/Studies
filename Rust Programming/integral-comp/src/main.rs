use core::f64;
use integral_comp::function::Function;
use integral_comp::proper_int::{
    LeftRectangleSolver, MiddleRectangleSolver, ProperIntegralOptions, ProperIntegralSolution,
    RightRectangleSolver, SimpsonSolver, TrapezoidSolver,
};
use integral_comp::solver::IntegralSolver;
use std::fmt::Write;
use std::io;
use std::rc::Rc;
use std::str::FromStr;

const FUNCTION_ARG_OFFSET: f64 = 0.00000001;
fn main() -> Result<(), Box<dyn std::error::Error>> {
    /*
    FUNCTIONS COMPILATION
     */
    let basic_polynomial_func = Rc::new(Function::new(
        |x| 1.0 + 2.0 * x - 3.0,
        "1+2x-3",
        None,
        Some(|x| x + x.powi(2) - 3.0 * x),
    ));
    let complex_polynomial_func = Rc::new(Function::new(
        |x| x.powi(3) + 4.81 * x.powi(2) - 17.37 * x + 5.38,
        "x^3 + 4.81x^2 - 17.37x + 5.38",
        None,
        Some(|x| x.powi(4) / 4.0 + 4.81 * x.powi(3) / 3.0 - 17.37 * x.powi(2) / 2.0 + 5.38 * x),
    ));
    let transcendent_trig_func = Rc::new(Function::new(
        |x| 5.0 * x.sin() - 2.0 * x.cos(),
        "5*sin(x) - 2*cos(x)",
        None,
        None,
    ));

    let mad_trig_func = Rc::new(Function::new(
        |x| (2.0 * x).sin() - 3.0 * x.cos() - 0.5 * x,
        "sin(2x)-3cos(x)-0.5x",
        None,
        None,
    ));

    let example_func = Rc::new(Function::new(
        |x| -2.0 * x.powi(3) - 5.0 * x.powi(2) + 7.0 * x - 13.0,
        "-2x^3-5x^2+7x-13",
        None,
        None,
    ));

    let inverse_cbrt = Rc::new(Function::new(
        |x| {
            let val = move_if_near_critical(x, 0.0, FUNCTION_ARG_OFFSET);
            1.0 / (2.0 * val.cbrt())
        },
        "1/(2*cbrt(x)) | MD",
        Some(|_x| true),
        None,
    ));

    let inverse_square = Rc::new(Function::new(
        |x| {
            let mut val = move_if_near_critical(x, 1.0, FUNCTION_ARG_OFFSET);
            val = move_if_near_critical(val, -1.0, FUNCTION_ARG_OFFSET);
            1.0 / (val.powi(2) - 1.0)
        },
        "1/(x^2-1) | MD",
        None,
        None,
    ));

    let inverse_monster = Rc::new(Function::new(
        |x| {
            let val = move_if_near_critical(x, 0.0, FUNCTION_ARG_OFFSET);
            1.0 / (val.powi(2).cbrt())
        },
        "1/x^(2/3) | MD",
        Some(|_x| true),
        None,
    ));

    let inverse_polynomial = Rc::new(Function::new(
        |x| {
            let mut val = move_if_near_critical(x, 0.0, FUNCTION_ARG_OFFSET);
            val = move_if_near_critical(val, 1.0, FUNCTION_ARG_OFFSET);
            val = move_if_near_critical(val, 2.0, FUNCTION_ARG_OFFSET);
            100.0 / (3.0*val * (val-1.0) * (val-2.0))
        },
        "100/(3x(x-1)(x-2)) | MD",
        Some(|_x| true),
        None,
    ));

    let func_pool = vec![
        basic_polynomial_func,
        example_func,
        complex_polynomial_func,
        transcendent_trig_func,
        mad_trig_func,
        inverse_cbrt,
        inverse_square,
        inverse_monster,
        inverse_polynomial
    ];

    loop {
        let func = input_function(&func_pool);
        let int_solver: Box<
            dyn IntegralSolver<
                SolverOptions = ProperIntegralOptions,
                Solution = ProperIntegralSolution,
            >,
        > = input_solver();

        let opts = input_proper_int_options(func);

        match opts.func.is_full_continuous() {
            true => (),
            false => {
                if !opts.func.continuity_check(opts.limits) {
                    println!(
                        "WARNING! Integral of {} doesn't converge on limits ({}, {})!",
                        opts.func, opts.limits.0, opts.limits.1
                    );
                    continue;
                }
            }
        };

        match opts.func.has_critical_points(opts.limits) {
            Some(x) => println!(
                "WARN! Function that is being integrated might have at least one critical point at around: {} ",
                x
            ),
            None => (),
        }

        match opts.func.has_critical_points_experimental(opts.limits) {
            Some(x) => println!(
                "WARN! EXPERIMENTAL! Function that is being integrated might have at least one critical point at around: {} ",
                x
            ),
            None => (),
        }

        println!("Solving with:{}", int_solver);
        let solution = int_solver.solve(opts);
        match solution {
            Ok(sol) => {
                println!("{}", sol);
            }
            Err(err) => println!("{}", err),
        }
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

fn input_function(pool: &Vec<Rc<Function>>) -> Rc<Function> {
    let mut inv_phrase = String::new();
    write!(
        inv_phrase,
        "Please, choose one of the following functions to integrate:\n"
    )
    .expect("Writing to string.");
    for (i, &ref func) in pool.iter().enumerate() {
        write!(inv_phrase, "{}| {} \n", i + 1, func).expect("Writing to string.");
    }
    loop {
        let index: usize = input_until_parsed::<usize>(&inv_phrase, "Please provide an integer!");
        if index >= 1 && index <= pool.len() {
            return pool[index - 1].clone();
        } else {
            println!("Provided index doesn't correspond to any function! Try again!");
        }
    }
}

fn input_solver(
) -> Box<dyn IntegralSolver<SolverOptions = ProperIntegralOptions, Solution = ProperIntegralSolution>>
{
    let phrase = "Choose prefered method: \n\
    1. Left Rectangles\n\
    2. Middle Rectangles \n\
    3. Right Rectangles \n\
    4. Trapezoid Method \n\
    5. Simpson Method \n";
    loop {
        let choice = input_until_parsed::<usize>(phrase, "Couldn't parse provided value as index");
        match choice {
            1 => return Box::new(LeftRectangleSolver {}),
            2 => return Box::new(MiddleRectangleSolver {}),
            3 => return Box::new(RightRectangleSolver {}),
            4 => {
                return Box::new(TrapezoidSolver {});
            }
            5 => {
                return Box::new(SimpsonSolver {});
            }
            _ => println!("Provided index doesn't correspond to any method!"),
        }
    }
}

fn input_proper_int_options(func: Rc<Function>) -> ProperIntegralOptions {
    let mut a;
    let mut b;
    let mut precision;
    loop {
        a = input_until_parsed::<f64>(
            "Please enter left (a) limit of integration:",
            "Failed to parse your input!",
        );
        b = input_until_parsed::<f64>(
            "Please enter right (b) limit of integration:",
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

    loop {
        precision =
            input_until_parsed::<f64>("Please enter precision:", "Failed to parse your input!");
        if precision <= 0.0 {
            println!("Precision must be a positive floating number!");
        } else {
            break;
        }
    }
    ProperIntegralOptions {
        func,
        limits: (a, b),
        precision,
    }
}

fn move_if_near_critical(x: f64, critical: f64, offset: f64) -> f64 {
    if (x - critical).abs() < offset {
        if x < critical {
            return x - offset;
        } else {
            return x + offset;
        }
    }
    x
}
