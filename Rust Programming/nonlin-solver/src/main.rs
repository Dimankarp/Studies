use nonlin_solver::equation::Equation;
use nonlin_solver::function::Function;
use nonlin_solver::system::System;
use nonlin_solver::{ConcreteTask, Config, TaskPool};
use std::io::Write;
use std::rc::Rc;
use std::{env, process};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    /*
    FUNCTIONS COMPILATION
     */
    let basic_polynomial_func = Rc::new(Function::new(
        1,
        |x| 1.0 + 2.0 * x[0] - 3.0,
        vec![|_x| 2.0],
        Some(vec![|_x| 0.0]),
        "1+2x-3",
    ));
    let complex_polynomial_func = Rc::new(Function::new(
        1,
        |x| x[0].powi(3) + 4.81 * x[0].powi(2) - 17.37 * x[0] + 5.38,
        vec![|x| 3.0 * x[0].powi(2) + 9.62 * x[0] - 17.37],
        Some(vec![|x| 6.0 * x[0] + 9.62]),
        "x^3 + 4.81x^2 - 17.37x + 5.38",
    ));
    let transcendent_trig_func = Rc::new(Function::new(
        1,
        |x| 5.0 * x[0].sin() - 2.0 * x[0].cos(),
        vec![|x| 5.0 * x[0].cos() + 2.0 * x[0].sin()],
        Some(vec![|x| -5.0 * x[0].sin() + 2.0 * x[0].cos()]),
        "5*sin(x) - 2*cos(x)",
    ));

    let mad_trig_func = Rc::new(Function::new(
        1,
        |x| (2.0 * x[0]).sin() - 3.0 * x[0].cos() - 0.5 * x[0],
        vec![|x| 2.0 * (2.0 * x[0]).cos() + 3.0 * x[0].sin() - 0.5],
        Some(vec![|x| -4.0 * (2.0 * x[0]).sin() + 3.0 * x[0].cos()]),
        "sin(2x)-3cos(x)-0.5x",
    ));

    let double_basic_polynomial_func = Rc::new(Function::new(
        2,
        |x| 5.0 * x[0] - 2.0 * x[1],
        vec![|_x| 5.0, |_x| -2.0],
        Some(vec![|_x| 0.0, |_x| 0.0]),
        "5x-2y",
    ));
    let double_complex_polynomial_func = Rc::new(Function::new(
        2,
        |x| {
            -12.0 * x[0].powi(3) + 5.0 * x[0].powi(2) - 2.0 * x[0] - 2.0 * x[1].powi(3)
                + 10.0 * x[0] * x[1]
        },
        vec![
            |x| -36.0 * x[0].powi(2) + 10.0 * x[0] - 2.0 + 10.0 * x[1],
            |x| -6.0 * x[1].powi(2) + 10.0 * x[0],
        ],
        None,
        "-12x^3+5x^2-2x-2y^3+10xy",
    ));
    let double_sin_noodle_func = Rc::new(Function::new(
        2,
        |x| x[0] + x[1].sin(),
        vec![|_x| 1.0, |x| x[1].cos()],
        None,
        "x+sin(y)",
    ));
    let double_cos_noodle_func = Rc::new(Function::new(
        2,
        |x| x[0].cos() + x[1],
        vec![|x| -1.0 * x[0].sin(), |_x| 1.0],
        None,
        "cos(x)+y",
    ));
    let double_oval_func = Rc::new(Function::new(
        2,
        |x| 5.0 * x[0].powi(2) + x[1].powi(2) - 5.0,
        vec![|x| 10.0 * x[0], |x| 2.0 * x[1]],
        None,
        "5x^2+y^2-5",
    ));
    let double_hyperbol_func = Rc::new(Function::new(
        2,
        |x| x[0].powi(2) - x[1].powi(2) - 0.5,
        vec![|x| 2.0 * x[0], |x| -2.0 * x[1]],
        None,
        "x^2-y^2-0.5",
    ));

    /*
    COMPILING EQUATION PAIRS
     */
    let polynomial_pair = vec![
        Equation::new(Rc::clone(&double_basic_polynomial_func), 0.0),
        Equation::new(Rc::clone(&double_complex_polynomial_func), 0.0),
    ];
    let noodle_pair = vec![
        Equation::new(Rc::clone(&double_cos_noodle_func), 0.0),
        Equation::new(Rc::clone(&double_sin_noodle_func), 0.0),
    ];
    let circlish_pair = vec![
        Equation::new(Rc::clone(&double_hyperbol_func), 0.0),
        Equation::new(Rc::clone(&double_oval_func), 0.0),
    ];

    //Not static to have checks in ::new calls.
    let task_pool: TaskPool = nonlin_solver::TaskPool {
        equations: vec![
            Equation::new(Rc::clone(&basic_polynomial_func), 0.0),
            Equation::new(Rc::clone(&complex_polynomial_func), 0.0),
            Equation::new(Rc::clone(&transcendent_trig_func), 0.0),
            Equation::new(Rc::clone(&mad_trig_func), 0.0),
        ],
        systems: vec![
            System::new(2, polynomial_pair),
            System::new(2, noodle_pair),
            System::new(2, circlish_pair),
        ],
    };
    let config: Config = Config::build(env::args()).unwrap_or_else(|err| {
        eprintln!("Failed to configure start: {err}");
        process::exit(1);
    });

    let concrete_task: ConcreteTask =
        nonlin_solver::input_concrete_task(&config.task_type, &task_pool)?;

    match nonlin_solver::run(&config, concrete_task) {
        Ok(solution) => {
            println!("{}", solution);

            match config.out_file {
                Some(mut file) => {
                    write!(file, "{}", solution)?;
                    println!("Printed solution to the file")
                }
                None => (),
            }
            process::exit(0);
        }
        Err(err) => {
            eprintln!("An error occured while running the program: {err}!");
            process::exit(1);
        }
    }
}
