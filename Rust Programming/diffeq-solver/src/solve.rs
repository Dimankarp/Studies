use crate::{diffeq::DifferentialEquation, misc::{get_table_str, get_table_str_shortened}};
use std::{error::Error, fmt::Display, iter::zip};

pub const MAX_POINTS: usize = 500;

pub const MILNE_MAX_ITERATIONS: usize = 10;
pub const MAX_RESULT_TABLE_ROWS: usize = 20;
pub trait DiffEquationSolver {
    type DiffEquationOptions;
    fn solve(
        &self,
        opts: &Self::DiffEquationOptions,
    ) -> Result<Box<dyn DiffEquationSolution>, Box<dyn Error>>;
}

pub struct RegularDiffEquationOptions {
    equation: DifferentialEquation,
    initial_point: (f64, f64),
    end_x: f64,
    initial_step: f64,
    precision: f64,
}

impl RegularDiffEquationOptions {
    pub fn new(
        equation: DifferentialEquation,
        initial_point: (f64, f64),
        end_x: f64,
        initial_step: f64,
        precision: f64,
    ) -> RegularDiffEquationOptions {
        if precision < 0.0 {
            panic!("Precision must be positive");
        }
        if initial_step < 0.0 {
            panic!("Initial step must be positive");
        }

        if initial_point.0 >= end_x {
            panic!(
                "Corrupted solving range is provided: {} : {}",
                initial_point.0, end_x
            );
        }
        RegularDiffEquationOptions {
            equation,
            initial_point,
            end_x,
            initial_step,
            precision,
        }
    }
    pub fn equation(&self) -> &DifferentialEquation {
        return &self.equation;
    }
    pub fn initial(&self) -> (f64, f64) {
        return self.initial_point;
    }
}

pub trait DiffEquationSolution: Display + Send {
    fn solution(&self) -> &Vec<(f64, f64)>;
    fn clone_dyn(&self) -> Box<dyn DiffEquationSolution>;
    fn display_name(&self) -> &str;
    fn met_precison(&self) -> bool;
}

impl Clone for Box<dyn DiffEquationSolution> {
    fn clone(&self) -> Self {
        self.clone_dyn()
    }
}

#[derive(Clone)]
struct EulerEquationSolution {
    points: Vec<(f64, f64)>,
    iterations: usize,
    final_step: f64,
    met_precision: bool,
}

impl Display for EulerEquationSolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {

        let rows: Vec<Vec<f64>> = self.points.iter().enumerate().map(|el| vec![el.0 as f64, el.1.0, el.1.1]).collect();
        let table_str = get_table_str_shortened(&vec!["i", "x_i", "y_i"], rows.iter().collect(), 8, 3, MAX_RESULT_TABLE_ROWS);
        write!(
            f,
            "\
        ======================================\n\
        Euler Method\n\
        --------------------------------------\n\
        {}\n\
        Iterations: {}\n\
        Final step: {}\n\
        {}\n
        ",
            if self.met_precision {""} else {"WARN! Failed to meet precision!"}, self.iterations, self.final_step, table_str
        )
    }
}

impl DiffEquationSolution for EulerEquationSolution {
    fn solution(&self) -> &Vec<(f64, f64)> {
        &self.points
    }

    fn clone_dyn(&self) -> Box<dyn DiffEquationSolution> {
        Box::new(self.clone())
    }

    fn display_name(&self) -> &str {
        "Euler Method"
    }
    fn met_precison(&self) -> bool {
        self.met_precision
    }
}

#[derive(Clone)]
struct ModifiedEulerEquationSolution {
    points: Vec<(f64, f64)>,
    iterations: usize,
    final_step: f64,
    met_precision: bool,
}

impl Display for ModifiedEulerEquationSolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let rows: Vec<Vec<f64>> = self.points.iter().enumerate().map(|el| vec![el.0 as f64, el.1.0, el.1.1]).collect();
        let table_str = get_table_str_shortened(&vec!["i", "x_i", "y_i"], rows.iter().collect(), 8, 3, MAX_RESULT_TABLE_ROWS);
        write!(
            f,
            "\
        ======================================\n\
        Modified Euler Method\n\
        --------------------------------------\n\
        {}\n\
        Iterations: {}\n\
        Final step: {}\n\
        {}\n
        ",
            if self.met_precision {""} else {"WARN! Failed to meet precision!"}, self.iterations, self.final_step, table_str
        )
    }
}

impl DiffEquationSolution for ModifiedEulerEquationSolution {
    fn solution(&self) -> &Vec<(f64, f64)> {
        &self.points
    }

    fn clone_dyn(&self) -> Box<dyn DiffEquationSolution> {
        Box::new(self.clone())
    }

    fn display_name(&self) -> &str {
        "Modified Euler Method"
    }
    fn met_precison(&self) -> bool {
        self.met_precision
    }
}

#[derive(Clone)]
struct MilneEquationSolution {
    points: Vec<(f64, f64)>,
    iterations: usize,
    final_step: f64,
    met_precision: bool,
}

impl Display for MilneEquationSolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let rows: Vec<Vec<f64>> = self.points.iter().enumerate().map(|el| vec![el.0 as f64, el.1.0, el.1.1]).collect();
        let table_str = get_table_str_shortened(&vec!["i", "x_i", "y_i"], rows.iter().collect(), 8, 3, MAX_RESULT_TABLE_ROWS);
        write!(
            f,
            "\
        ======================================\n\
        Mline Method\n\
        --------------------------------------\n\
        {}\n\
        Iterations: {}\n\
        Final step: {}\n\
        {}\n
        ",
            if self.met_precision {""} else {"WARN! Failed to meet precision!"}, self.iterations, self.final_step, table_str
        )
    }
}

impl DiffEquationSolution for MilneEquationSolution {
    fn solution(&self) -> &Vec<(f64, f64)> {
        &self.points
    }

    fn clone_dyn(&self) -> Box<dyn DiffEquationSolution> {
        Box::new(self.clone())
    }

    fn display_name(&self) -> &str {
        "Milne Method"
    }

    fn met_precison(&self) -> bool {
        self.met_precision
    }
}


struct SolutionResult {
    result: Vec<(f64, f64)>,
    iterations: usize,
    end_step: f64,
    met_precision: bool,
}

fn solve_with_runge<F: Fn(&Vec<f64>, (f64, f64), f64) -> Vec<f64>>(
    interval: (f64, f64),
    start_step: f64,
    start_point: (f64, f64),
    precision: f64,
    runge_coeff: u8,
    func: F,
) -> SolutionResult {
    let mut cur_step = start_step;
    let mut cur_x = generate_x_vec(interval, cur_step);
    let mut cur_y = func(&cur_x, start_point, cur_step);

    let mut next_x = generate_x_vec(interval, cur_step / 2.0);
    let mut next_y = func(&next_x, start_point, cur_step / 2.0);

    let mut iterations = 1;
    let mut points_num = ((interval.1 - interval.0) / cur_step) as usize;
    while !runge_precision_check(precision, runge_coeff, &cur_y, &next_y) && points_num < MAX_POINTS
    {
        cur_x = next_x;
        cur_y = next_y;
        cur_step = cur_step / 2.0;

        next_x = generate_x_vec(interval, cur_step / 2.0);
        next_y = func(&next_x, start_point, cur_step / 2.0);

        iterations += 1;
        points_num = ((interval.1 - interval.0) / cur_step) as usize;
    }

    return SolutionResult {
        result: zip(cur_x, cur_y).into_iter().collect(),
        iterations,
        end_step: cur_step,
        met_precision: points_num <= MAX_POINTS,
    };
}

fn generate_x_vec(interval: (f64, f64), start_step: f64) -> Vec<f64> {
    let mut cur_x = vec![];
    let mut cur = interval.0;
    for _ in 0..=((interval.1 - interval.0) / start_step) as u32 {
        cur_x.push(cur);
        cur += start_step;
    }
    return cur_x;
}

fn runge_precision_check(
    precision: f64,
    runge_coeff: u8,
    full_step: &Vec<f64>,
    half_step: &Vec<f64>,
) -> bool {
    for i in 0..full_step.len() {
        if (full_step[i] - half_step[i * 2]).abs() / (2_i32.pow(runge_coeff as u32) - 1) as f64
            > precision
        {
            return false;
        }
    }
    true
}

fn solve_with_precise<F: Fn(&Vec<f64>, (f64, f64), f64) -> Vec<f64>>(
    interval: (f64, f64),
    start_step: f64,
    initial_point: (f64, f64),
    precision: f64,
    func: F,
    equation: &DifferentialEquation,
) -> SolutionResult {
    let sol_constant = equation.constant(initial_point);

    let mut cur_step = start_step;
    let mut cur_x = generate_x_vec(interval, cur_step);
    let mut cur_points = func(&cur_x, initial_point, cur_step);

    let mut sol_points = cur_x
        .iter()
        .map(|x| equation.solution().fun(x.clone(), sol_constant))
        .collect();

    let mut iterations = 1;
    let mut points_num = cur_points.len();
    while !solution_precision_check(precision, &cur_points, &sol_points) && points_num < MAX_POINTS
    {
        cur_step = cur_step / 2.0;

        cur_x = generate_x_vec(interval, cur_step);
        cur_points = func(&cur_x, initial_point, cur_step);

        sol_points = cur_x
            .iter()
            .map(|x| equation.solution().fun(x.clone(), sol_constant))
            .collect();

        iterations += 1;
        points_num = cur_points.len()
    }

    return SolutionResult {
        result: zip(cur_x, cur_points).into_iter().collect(),
        iterations,
        end_step: cur_step,
        met_precision: points_num <= MAX_POINTS,
    };
}

fn solution_precision_check(
    precision: f64,
    check_points: &Vec<f64>,
    solution_points: &Vec<f64>,
) -> bool {
    let mut max_diff: f64 = 0.0;
    for (chk, sol) in zip(check_points, solution_points) {
        max_diff = max_diff.max((chk - sol).abs())
    }
    return max_diff < precision;
}
pub enum Solvers {
    Euler,
    ModifiedEuler,
    Milne,
}

impl DiffEquationSolver for Solvers {
    type DiffEquationOptions = RegularDiffEquationOptions;

    fn solve(
        &self,
        opts: &Self::DiffEquationOptions,
    ) -> Result<Box<dyn DiffEquationSolution>, Box<dyn Error>> {
        match self {
            Solvers::Euler => {
                let euler_closure = |x_vec: &Vec<f64>, point: (f64, f64), step: f64| {
                    euler_approximation(
                        &opts.equation.func().fun_closure(),
                        x_vec,
                        opts.initial_point,
                        step,
                    )
                };

                let result = solve_with_runge(
                    (opts.initial_point.0, opts.end_x),
                    opts.initial_step,
                    opts.initial_point,
                    opts.precision,
                    1,
                    euler_closure,
                );
                return Ok(Box::new(EulerEquationSolution {
                    points: result.result,
                    iterations: result.iterations,
                    final_step: result.end_step,
                    met_precision: result.met_precision,
                }));
            }
            Solvers::ModifiedEuler => {
                let modifeuler_closure = |x_vec: &Vec<f64>, point: (f64, f64), step: f64| {
                    modified_euler_approximation(
                        opts.equation().func().fun_closure(),
                        x_vec,
                        opts.initial_point,
                        step,
                    )
                };
                let result = solve_with_runge(
                    (opts.initial_point.0, opts.end_x),
                    opts.initial_step,
                    opts.initial_point,
                    opts.precision,
                    1,
                    modifeuler_closure,
                );
                return Ok(Box::new(ModifiedEulerEquationSolution {
                    points: result.result,
                    iterations: result.iterations,
                    final_step: result.end_step,
                    met_precision: result.met_precision,
                }));
            }
            Solvers::Milne => {
                let milne_closure = |x_vec: &Vec<f64>, point: (f64, f64), step: f64| {
                    let initial_points = modified_euler_approximation(
                        opts.equation().func().fun_closure(),
                        &x_vec[..4.min(x_vec.len())].to_vec(),
                        point,
                        step,
                    );
                    if initial_points.len() == 4 {
                        milne_approximation(
                            opts.equation().func().fun_closure(),
                            x_vec,
                            initial_points
                                .try_into()
                                .expect("There is a check of length"),
                            opts.precision,
                            step,
                        )
                    } else {
                        return initial_points;
                    }
                };
                let result = solve_with_precise(
                    (opts.initial_point.0, opts.end_x),
                    opts.initial_step,
                    opts.initial_point,
                    opts.precision,
                    milne_closure,
                    &opts.equation,
                );
                return Ok(Box::new(MilneEquationSolution {
                    points: result.result,
                    iterations: result.iterations,
                    final_step: result.end_step,
                    met_precision: result.met_precision,
                }));
            }
        }
    }
}

fn euler_approximation(
    func: impl Fn(f64, f64) -> f64,
    x_vec: &Vec<f64>,
    initial_point: (f64, f64),
    step: f64,
) -> Vec<f64> {
    let mut result = vec![initial_point.1];
    for i in 1..x_vec.len() {
        let last_y = result.last().expect("Start point is passed");
        result.push(last_y + step * func(x_vec[i - 1], *last_y));
    }
    return result;
}

fn modified_euler_approximation(
    func: impl Fn(f64, f64) -> f64,
    x_vec: &Vec<f64>,
    initial_point: (f64, f64),
    step: f64,
) -> Vec<f64> {
    let mut result = vec![initial_point.1];
    for i in 1..x_vec.len() {
        let last_y = result.last().expect("Start point is passed");
        let last_f = func(x_vec[i - 1], *last_y);
        result.push(last_y + step / 2.0 * (last_f + func(x_vec[i], last_y + step * last_f)));
    }
    return result;
}

fn milne_approximation(
    func: impl Fn(f64, f64) -> f64,
    x_vec: &Vec<f64>,
    initial_points: [f64; 4],
    precision: f64,
    step: f64,
) -> Vec<f64> {
    let mut result: Vec<f64> = initial_points.to_vec();
    for i in 4..x_vec.len() {
        let f_1_before = func(x_vec[i - 1], result[i - 1]);
        let f_2_before = func(x_vec[i - 2], result[i - 2]);
        let f_3_before = func(x_vec[i - 3], result[i - 3]);

        let mut iterations = 0;
        let mut val =
            result[i - 4] + 4.0 / 3.0 * step * (2.0 * f_3_before - f_2_before + 2.0 * f_1_before);
        let mut next_val =
            result[i - 2] + step / 3.0 * (f_2_before + 4.0 * f_1_before + func(x_vec[i], val));
        while (next_val - val).abs() > precision && iterations < MILNE_MAX_ITERATIONS {
            val = next_val;
            next_val =
                result[i - 2] + step / 3.0 * (f_2_before + 4.0 * f_1_before + func(x_vec[i], val));
            iterations += 1;
        }
        result.push(next_val);
    }
    return result;
}
