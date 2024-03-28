use equation::Equation;
use regex::Regex;
use std::fmt::Debug;
use std::{
    error::Error,
    fs::File,
    io::{self, Read},
    num::ParseFloatError,
};
use system::System;

pub mod equation;
pub mod function;
mod solver;
pub mod system;

#[derive(Clone)]
pub enum TaskType {
    Equation(EquationSolvingMethods),
    SystemOfEquations(SystemSolvingMethods),
}

impl Debug for TaskType {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Equation(arg0) => write!(f, "{:?} Method", arg0),
            Self::SystemOfEquations(arg0) => write!(f, "{:?} Method", arg0),
        }
    }
}

pub struct TaskPool {
    pub equations: Vec<Equation>,
    pub systems: Vec<System>,
}

#[derive(Clone, Debug)]
pub enum EquationSolvingMethods {
    Bisection,
    Secant,
    Iteration,
}
#[derive(Clone, Debug)]
pub enum SystemSolvingMethods {
    Newton,
}
impl TryFrom<i32> for EquationSolvingMethods {
    type Error = ();

    fn try_from(v: i32) -> Result<Self, Self::Error> {
        match v {
            x if x == EquationSolvingMethods::Bisection as i32 => {
                Ok(EquationSolvingMethods::Bisection)
            }
            x if x == EquationSolvingMethods::Secant as i32 => Ok(EquationSolvingMethods::Secant),
            x if x == EquationSolvingMethods::Iteration as i32 => {
                Ok(EquationSolvingMethods::Iteration)
            }
            _ => Err(()),
        }
    }
}
impl TryFrom<i32> for SystemSolvingMethods {
    type Error = ();
    fn try_from(v: i32) -> Result<Self, Self::Error> {
        match v {
            x if x == SystemSolvingMethods::Newton as i32 => Ok(SystemSolvingMethods::Newton),
            _ => Err(()),
        }
    }
}

pub struct Config {
    in_file: Option<File>,
    pub out_file: Option<File>,
    pub task_type: TaskType,
    pub only_plot: bool
}

impl Config {
    pub fn build(mut args: impl Iterator<Item = String>) -> Result<Self, String> {
        args.next(); //Skipping program name
        let mut in_file = None;
        let mut out_file = None;
        let mut task_type = TaskType::Equation(EquationSolvingMethods::Bisection);
        let mut only_plot = false;

        while let Some(arg) = args.next() {
            match arg.as_str() {
                "-help" | "-h" | "-info" => {
                    println!(
                        "-help | -h | -info - Shows a help menu\n\
                    -p - Only plot chosen equation/system around (0;0)\n\
                    -f <file> - Reads method options from the <file>\n\
                    -o <file> - Outputs solution log to <file> \n\
                    -e <0..2> - Find root of the equation with: \n\
                        <0> - Bisection method (Default)\n\
                        <1> - Secant method\n\
                        <2> - Iterative method \n\
                    -s <0..0> - Find solution to the system with \n\
                        <0> - Newton method \n\
                    "
                    );
                    std::process::exit(0);
                }

                "-f" | "-o" => {
                    let path = match args.next() {
                        Some(arg) => arg,
                        None => {
                            return Err(
                                "Filepath wasn't immediately provided after -f key".to_string()
                            )
                        }
                    };

                    match arg.as_str() {
                        "-f" => {
                            in_file = match File::open(&path) {
                                Ok(arg) => Some(arg),
                                Err(err) => {
                                    return Err(String::from(format!(
                                        "Failed to open file {path} because of: {err}"
                                    )));
                                }
                            };
                        }
                        "-o" => {
                            out_file = match File::create(&path) {
                                Ok(arg) => Some(arg),
                                Err(err) => {
                                    return Err(String::from(format!(
                                        "Failed to create or truncate file {path} because of: {err}"
                                    )));
                                }
                            };
                        }
                        _ => {
                            panic!("Must be unreachable!")
                        }
                    }
                }
                "-s" => {
                    let method_id: i32 = match args.next() {
                        Some(arg) => match arg.parse() {
                            Ok(val) => val,
                            Err(_) => return Err("Couldn;t parse provided index.".to_string()),
                        },
                        None => {
                            return Err(
                                "Method ID wasn't immediately provided after -s key".to_string()
                            )
                        }
                    };
                    task_type = match SystemSolvingMethods::try_from(method_id) {
                        Ok(method) => TaskType::SystemOfEquations(method),
                        Err(_) => return Err("Couldn't get method by provided index.".to_string()),
                    }
                }
                "-e" => {
                    let method_id: i32 = match args.next() {
                        Some(arg) => match arg.parse() {
                            Ok(val) => val,
                            Err(_) => return Err("Couldn;t parse provided index.".to_string()),
                        },
                        None => {
                            return Err(
                                "Method ID wasn't immediately provided after -s key".to_string()
                            )
                        }
                    };
                    task_type = match EquationSolvingMethods::try_from(method_id) {
                        Ok(method) => TaskType::Equation(method),
                        Err(_) => return Err("Couldn't get method by provided index.".to_string()),
                    }
                }
                "-p" =>{
                    only_plot = true;
                }
                _ => {
                    return Err(String::from(format!("Invalid argument provided: {}", arg)));
                }
            }
        }
        return Ok(Config {
            in_file,
            out_file,
            task_type,
            only_plot
        });
    }
}

pub struct EquationSolution {
    pub x: f64,
    pub f_x: f64,
    pub iterations: usize,
    pub equation: Equation,
    pub root_interval: (f64, f64),
}

pub struct SystemSolution {
    pub x_vec: Vec<f64>,
    pub x_diff_vec: Vec<f64>,
    pub iterations: usize,
    pub x_approx_vec: Vec<f64>,
    pub system: System,
}

impl std::fmt::Display for EquationSolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "SOLUTION:\n\n\
            x -> {x:>10}\n\n\
            f(x) -> {y:>10} \n\n\
            Was found in {} iterations.",
            self.iterations,
            x = self.x,
            y = self.f_x,
        )
    }
}

impl std::fmt::Display for SystemSolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "SOLUTION:\n\n\
            (X) -> {:?}\n\n\
            |X^(k)-X^(k-1)| -> {:?}\n\n\
            Was found in {} iterations.",
            self.x_vec, self.x_diff_vec, self.iterations,
        )
    }
}

pub enum Solution {
    EquationSolution(EquationSolution),
    SystemSolution(SystemSolution),
}

impl std::fmt::Display for Solution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Solution::EquationSolution(eq) => {
                write!(f, "{}", eq)
            }
            Solution::SystemSolution(sys) => {
                write!(f, "{}", sys)
            }
        }
    }
}
enum MethodOptions {
    EquationMethodOptions(EquationMethodOptions),
    SystemMethodOptions(SystemMethodOptions),
}
#[derive(Debug)]
enum EquationTerminationCriteria {
    ByArgumentDiff,
    ByFunctionDiff,
}
impl TryFrom<i32> for EquationTerminationCriteria {
    type Error = ();
    fn try_from(v: i32) -> Result<Self, Self::Error> {
        match v {
            x if x == EquationTerminationCriteria::ByArgumentDiff as i32 => Ok(EquationTerminationCriteria::ByArgumentDiff),
            x if x == EquationTerminationCriteria::ByFunctionDiff as i32 => Ok(EquationTerminationCriteria::ByFunctionDiff),
            _ => Err(()),
        }
    }
}
enum SystemTerminationCriteria {
    ByArgumentDiff,
}
struct EquationMethodOptions {
    root_interval: (f64, f64),
    precision: f64,
    root_approx: Option<f64>,
    term_criteria: EquationTerminationCriteria,
}

struct SystemMethodOptions {
    precision: f64,
    root_approxs: Vec<f64>,
    term_criteria: SystemTerminationCriteria,
}

impl EquationMethodOptions {
    pub fn new(
        root_interval: (f64, f64),
        precision: f64,
        term_criteria: EquationTerminationCriteria,
        root_approx: Option<f64>,
    ) -> Self {
        if precision < 0.0 {
            panic!("Precision must be a positive float.");
        }
        if root_interval.0 >= root_interval.1 {
            panic!("Invalid root interval provided");
        }

        match root_approx {
            Some(arg) => {
                if arg < root_interval.0 || arg > root_interval.1 {
                    panic!("Provided root approximation is not inside the root interval!")
                }
            }
            None => (),
        }
        EquationMethodOptions {
            root_approx,
            root_interval,
            precision,
            term_criteria,
        }
    }
}

impl SystemMethodOptions {
    pub fn new(
        equations_count: usize,
        precision: f64,
        term_criteria: SystemTerminationCriteria,
        root_approxs: Vec<f64>,
    ) -> Self {
        if precision < 0.0 {
            panic!("Precision must be a positive float.");
        }
        if equations_count != root_approxs.len() {
            panic!("Number of equations is different to the number of provided approximations!");
        }
        SystemMethodOptions {
            precision: precision,
            root_approxs,
            term_criteria,
        }
    }
}

pub enum ConcreteTask {
    Equation(Equation, EquationSolvingMethods),
    System(System, SystemSolvingMethods),
}
pub fn run<'a>(config: &Config, concrete_task: ConcreteTask) -> Result<Solution, Box<dyn Error>> {
    println!("Solving with |{:?}|", config.task_type);

    if config.only_plot{

    }

    let options: MethodOptions;
    match &config.in_file {
        Some(file) => {
            options = input_from_file(&file, &config.task_type.clone())?;
            println!("Picked up options from the file.");
        }
        None => {
            options = input_from_stdin(&config.task_type)?;
        }
    }

    match options {
        MethodOptions::EquationMethodOptions(ref options) => {
            println!("With precision e={}", options.precision);
            println!(
                "In the interval ({}; {})",
                options.root_interval.0, options.root_interval.1
            );
            println!("With termination criteria: {:?}", options.term_criteria)
        }
        MethodOptions::SystemMethodOptions(ref options) => {
            println!("With precision e={}", options.precision);
            println!("Starting approximation: {:?}", options.root_approxs);
        }
    }
    let solution = match concrete_task {
        ConcreteTask::Equation(ref equation, ref method) => match method {
            EquationSolvingMethods::Bisection => solver::bisection_solve(&equation, &options),
            EquationSolvingMethods::Secant => solver::secant_solve(&equation, &options),
            EquationSolvingMethods::Iteration => solver::iterative_solve(&equation, &options),
        },
        ConcreteTask::System(ref system, ref method) => match method {
            SystemSolvingMethods::Newton => solver::newton_solve(&system, &options),
        },
    };
    match solution {
        Ok(solution) => return Ok(solution),
        Err(err) => Err(err),
    }
}

pub fn input_concrete_task(
    task_type: &TaskType,
    pool: &TaskPool,
) -> Result<ConcreteTask, Box<dyn Error>> {
    match task_type {
        TaskType::Equation(ref method) => {
            for (pos, e) in pool.equations.iter().enumerate() {
                println!("{pos}.| {e} \n")
            }
            println!("Please, enter the number of the equation you want to solve:");
            let eq_num: usize = loop {
                let mut input = String::new();
                io::stdin().read_line(&mut input)?;
                let num: usize = match input.trim().parse() {
                    Ok(num) => num,
                    Err(_) => {
                        println!("Failed to parse provided value as usize. Please, try again:");
                        continue;
                    }
                };
                if num < pool.equations.len() {
                    break num;
                } else {
                    println!("There is no task with number {num}. Please, try again:");
                    continue;
                }
            };
            println!("Trying to solve:\n{}", &pool.equations[eq_num]);
            Ok(ConcreteTask::Equation(
                pool.equations[eq_num].clone(),
                method.clone(),
            ))
        }
        TaskType::SystemOfEquations(ref method) => {
            for (pos, e) in pool.systems.iter().enumerate() {
                println!("|{pos}.| \n{e} \n --- \n")
            }
            println!("Please, enter the number of the system you want to solve:");
            let eq_num: usize = loop {
                let mut input = String::new();
                io::stdin().read_line(&mut input)?;
                let num: usize = match input.trim().parse() {
                    Ok(num) => num,
                    Err(_) => {
                        println!("Failed to parse provided value as usize. Please, try again:");
                        continue;
                    }
                };
                if num < pool.systems.len() {
                    break num;
                } else {
                    println!("There is no task with number {num}. Please, try again:");
                    continue;
                }
            };
            println!("Trying to solve:\n{}", &pool.systems[eq_num]);
            Ok(ConcreteTask::System(
                pool.systems[eq_num].clone(),
                method.clone(),
            ))
        }
    }
}

fn input_from_file(mut file: &File, task_type: &TaskType) -> Result<MethodOptions, String> {
    let mut file_input = String::new();
    if let Err(err) = file.read_to_string(&mut file_input) {
        return Err(format!("Failed to read provided file because of: {err}"));
    }

    let precision: f64;

    let precision_re =
        Regex::new(r"(?m)^e=(\d+\.?\d*)").expect("The regex must compile. It's tested");
    if let Some((_, [epsilon])) = precision_re
        .captures_iter(&file_input)
        .map(|c| c.extract())
        .next()
    {
        //Reading first match;
        precision = match epsilon.parse() {
            Ok(val) => val,
            Err(_) => {
                return Err(format!(
                    "Failed to parse precision: {epsilon}. Please use valid float form."
                ));
            }
        };
    } else {
        return Err(format!(
            "Failed to find precision line in the file. Expected to see: \"e=XXX.XXX\""
        ));
    }

    match task_type {
        TaskType::Equation(_method) => {
            let interval: (f64, f64);
            let mut root_approx: Option<f64> = None;
            let interval_re = Regex::new(r"(?m)^\(a;b\)=\((-?\d+\.?\d*);(-?\d+\.?\d*)\)")
                .expect("The regex must compile. It's tested");
            if let Some((_, [a_str, b_str])) = interval_re
                .captures_iter(&file_input)
                .map(|c| c.extract())
                .next()
            {
                //Reading first match;
                interval = (
                    match a_str.parse() {
                        Ok(val) => val,
                        Err(_) => {
                            return Err(format!(
                "Failed to parse left interval border: {a_str}. Please use valid float form."
            ));
                        }
                    },
                    match b_str.parse() {
                        Ok(val) => val,
                        Err(_) => {
                            return Err(format!(
                "Failed to parse right interval border: {b_str}. Please use valid float form."
            ));
                        }
                    },
                );
            } else {
                return Err(format!(
        "Failed to find interval line in the file. Expected to see: \"(a;b)=(XXX.XXX;XXX.XXX)\""
    ));
            }
            let root_approx_re =
                Regex::new(r"(?m)^x_0=(-?\d+\.?\d*)").expect("The regex must compile. It's tested");

            if let Some((_, [root_str])) = root_approx_re
                .captures_iter(&file_input)
                .map(|c| c.extract())
                .next()
            {
                root_approx = Some(match root_str.parse() {
                    Ok(val) => val,
                    Err(_) => {
                        return Err(format!(
                    "Failed to parse root approximation: {root_str}. Please use valid float form."
                ));
                    }
                });
            }
            Ok(MethodOptions::EquationMethodOptions(
                EquationMethodOptions::new(interval, precision, EquationTerminationCriteria::ByArgumentDiff, root_approx),
            ))
        }
        TaskType::SystemOfEquations(_sys) => {
            let approx_vec: Vec<f64>;
            let approx_vec_re =
                Regex::new(r"(?m)---(?:\n|\r\n)((?:\s*[-+]?\d+\.?\d*\s*\n?)+)(?:\n|\r\n)---")
                    .expect("The regex must compile. It's tested");
            if let Some((_, [vec_str])) = approx_vec_re
                .captures_iter(&file_input)
                .map(|c| c.extract())
                .next()
            {
                approx_vec = match vec_str
                    .split_ascii_whitespace()
                    .map(|num| (*num).parse::<f64>())
                    .collect::<Result<Vec<f64>, ParseFloatError>>()
                {
                    Ok(val) => val,
                    Err(err) => {
                        return Err(format!("Failed to parse vec element: {err}"));
                    }
                }
            } else {
                return Err(format!("Failed to find vector lines in the file. Expected to see: \n---\nX.XXX X.XXX X.XXX...\n...\nX.XXX X.XXX X.XXX\n---\n"));
            }
            Ok(MethodOptions::SystemMethodOptions(
                SystemMethodOptions::new(approx_vec.len(), precision, SystemTerminationCriteria::ByArgumentDiff, approx_vec),
            ))
        }
    }
}

fn input_from_stdin(task_type: &TaskType) -> Result<MethodOptions, Box<dyn Error>> {
    println!("Please, enter required precision (e) as float:");
    let precision: f64 = loop {
        let mut input = String::new();
        io::stdin().read_line(&mut input)?;

        let parsed_input: f64 = match input.trim().parse() {
            Ok(num) => num,
            Err(_) => {
                println!("Failed to parse provided value as float. Please, try again:");
                continue;
            }
        };
        if parsed_input <= 0.0 {
            println!("Precision must be a float bigger than zero.");
            continue;
        }
        break parsed_input;
    };

    match task_type {
        TaskType::Equation(_eq) => {
            println!("Please choose termination criteria:");
            println!("1. By Argument difference \n2. By Function difference");
            let term_criteria: EquationTerminationCriteria = loop {
                let mut input = String::new();
                io::stdin().read_line(&mut input)?;
                let index: i32 = match input.trim().parse() {
                    Ok(num) => num,
                    Err(_) => {
                        println!("Failed to parse provided value as usize. Please, try again:");
                        continue;
                    }
                };
                break match EquationTerminationCriteria::try_from(index-1) {
                    Ok(criteria) => criteria,
                    Err(_) => {
                        println!("Invalid Number provided!");
                        continue;
                    },
                }

            };
            
            let interval: (f64, f64) = loop {
                println!("Please, enter the left border 'a' of the root interval (a;b):");
                let a: f64 = loop {
                    let mut input = String::new();
                    io::stdin().read_line(&mut input)?;
                    break match input.trim().parse() {
                        Ok(num) => num,
                        Err(_) => {
                            println!("Failed to parse provided value as float. Please, try again:");
                            continue;
                        }
                    };
                };
                println!("Please, enter the left border 'b' of the root interval (a;b):");
                let b: f64 = loop {
                    let mut input = String::new();
                    io::stdin().read_line(&mut input)?;
                    break match input.trim().parse() {
                        Ok(num) => num,
                        Err(_) => {
                            println!("Failed to parse provided value as float. Please, try again:");
                            continue;
                        }
                    };
                };
                if a >= b {
                    println!("Invalid interval provided. Border a must be less than b");
                    continue;
                }
                break (a, b);
            };

            println!("Do you want to provide root approximation? (Y/N)");
            let approx_needed: bool = loop {
                let mut input = String::new();
                io::stdin().read_line(&mut input)?;
                break match input.trim().to_lowercase().as_str() {
                    "y" => true,
                    "n" => false,
                    _ => {
                        println!("Failed to parse provided value as Y/N. Please, try again:");
                        continue;
                    }
                };
            };
            let mut approx: Option<f64> = None;
            if approx_needed {
                println!("Please provide root approximation:");
                approx = loop {
                    let mut input = String::new();
                    io::stdin().read_line(&mut input)?;
                    let parsed: Option<f64> = match input.trim().parse() {
                        Ok(num) => Some(num),
                        Err(_) => {
                            println!("Failed to parse provided value as float. Please, try again:");
                            continue;
                        }
                    };
                    if parsed.unwrap() < interval.0 || parsed.unwrap() > interval.1 {
                        println!(
                            "Root approximation must lie within provided interval ({}; {})",
                            interval.0, interval.1
                        );
                        continue;
                    }
                    break parsed;
                };
            }
            Ok(MethodOptions::EquationMethodOptions(
                EquationMethodOptions::new(interval, precision, term_criteria, approx),
            ))
        }
        TaskType::SystemOfEquations(_) => {
            let mut approx_vec: Vec<f64> = vec![];
            for i in 0..2 {
                println!("Please, enter the x_{} root approx:", i + 1);
                let approx: f64 = loop {
                    let mut input = String::new();
                    io::stdin().read_line(&mut input)?;
                    break match input.trim().parse() {
                        Ok(num) => num,
                        Err(_) => {
                            println!("Failed to parse provided value as float. Please, try again:");
                            continue;
                        }
                    };
                };
                approx_vec.push(approx);
            }
            Ok(MethodOptions::SystemMethodOptions(
                SystemMethodOptions::new(approx_vec.len(), precision, SystemTerminationCriteria::ByArgumentDiff, approx_vec),
            ))
        }
    }
}
