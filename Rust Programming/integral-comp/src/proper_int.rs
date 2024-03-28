use std::{error::Error, fmt::Display, rc::Rc};

use crate::{function::Function, solver::IntegralSolver};

const START_N: u64 = 4;
const MAX_ITER: usize = 20;
pub struct ProperIntegralOptions {
    pub limits: (f64, f64),
    pub func: Rc<Function>,
    pub precision: f64,
}

pub struct ProperIntegralSolution {
    pub opts: ProperIntegralOptions,
    pub result: f64,
    pub end_n: u64,
    pub diff: f64,
}

impl Display for ProperIntegralSolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
        ------------------------------\n\
        Solution for proper integral of {}\n\
        with limits ({}, {}): \n\
        Result: {}\n\
        Precision: {}\n\
        Found with n: {}\n\
        Last difference: {}\n\
        ------------------------------\n\
        ",
            self.opts.func,
            self.opts.limits.0,
            self.opts.limits.1,
            self.result,
            self.opts.precision,
            self.end_n,
            self.diff
        )?;
        if let Some(precise_int) = self.opts.func.try_integrating(self.opts.limits) {
            write!(
                f,
                "\
            ------------------------------\n\
            Precise Value: {}\n\
            ------------------------------\n",
                precise_int
            )?;
        };
        Ok(())
    }
}

fn solve_with_runge(
    opts: ProperIntegralOptions,
    solve_fn: fn(&Rc<Function>, (f64, f64), u64) -> Result<f64, Box<dyn Error>>,
    complexity_coeff: u32,
) -> Result<ProperIntegralSolution, Box<dyn Error>> {
    let mut iterations = 0;
    let runge_divisor = (2_u16.pow(complexity_coeff) - 1) as f64;
    let mut cur_n = START_N;
    let mut prev_f = solve_fn(&opts.func, opts.limits, cur_n)?;
    cur_n *= 2;
    let mut next_f = solve_fn(&opts.func, opts.limits, cur_n)?;
    let mut last_f_diff = (next_f - prev_f).abs() / runge_divisor;
    let mut new_f_diff = last_f_diff;
    loop {
        iterations += 1;
        //println!("n: {}, last_f:{}, next_f:{}", cur_n, next_f, prev_f);
        if (next_f - prev_f).abs() / runge_divisor < opts.precision {
            return Ok(ProperIntegralSolution {
                opts,
                result: next_f,
                end_n: cur_n,
                diff: (next_f - prev_f).abs() / runge_divisor,
            });
        }
        if iterations >= MAX_ITER {
            return Err(format!(
                "Failed to meet required precision in {} iterations",
                MAX_ITER
            )
            .into());
        }
        prev_f = next_f;
        cur_n *= 2;
        next_f = solve_fn(&opts.func, opts.limits, cur_n)?;
        last_f_diff = new_f_diff;
        new_f_diff = (next_f - prev_f).abs() / runge_divisor;
        if new_f_diff.abs() - last_f_diff.abs() > 0.0 {
            println!("WARN! An estimated difference from calculated integral with the true answer has grown on this iteration! The integral might diverge!");
        }
    }
}

/*
LEFT RECTANGLE SOLVER
*/
pub struct LeftRectangleSolver {}

impl LeftRectangleSolver {
    fn left_rectangle(
        func: &Rc<Function>,
        limits: (f64, f64),
        n: u64,
    ) -> Result<f64, Box<dyn Error>> {
        let width = (limits.1 - limits.0) / (n as f64);
        let mut sum = 0.0;
        let mut l_x = limits.0;
        for _i in 0..n {
            sum += func.fun(l_x) * width;
            l_x += width;
        }
        return Ok(sum);
    }
}

impl IntegralSolver for LeftRectangleSolver {
    type SolverOptions = ProperIntegralOptions;

    type Solution = ProperIntegralSolution;

    fn solve(&self, opts: Self::SolverOptions) -> Result<Self::Solution, Box<dyn Error>> {
        solve_with_runge(opts, Self::left_rectangle, 1)
    }
}

impl Display for LeftRectangleSolver {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Method of approximation by Left-dominant Rectangles.")
    }
}

/*
RIGHT RECTANGLE SOLVER
 */

pub struct RightRectangleSolver {}

impl RightRectangleSolver {
    fn right_rectangle(
        func: &Rc<Function>,
        limits: (f64, f64),
        n: u64,
    ) -> Result<f64, Box<dyn Error>> {
        let width = (limits.1 - limits.0) / n as f64;
        let mut sum = 0.0;
        let mut l_x = limits.0 + width;
        for _i in 0..n {
            sum += func.fun(l_x) * width;
            l_x += width;
        }
        return Ok(sum);
    }
}

impl IntegralSolver for RightRectangleSolver {
    type SolverOptions = ProperIntegralOptions;

    type Solution = ProperIntegralSolution;

    fn solve(&self, opts: Self::SolverOptions) -> Result<Self::Solution, Box<dyn Error>> {
        solve_with_runge(opts, Self::right_rectangle, 1)
    }
}

impl Display for RightRectangleSolver {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Method of approximation by Right-dominant Rectangles.")
    }
}

/*
MIDDLE RECTANGLE SOLVER
*/
pub struct MiddleRectangleSolver {}

impl MiddleRectangleSolver {
    fn middle_rectangle(
        func: &Rc<Function>,
        limits: (f64, f64),
        n: u64,
    ) -> Result<f64, Box<dyn Error>> {
        let width = (limits.1 - limits.0) / n as f64;
        let mut sum = 0.0;
        let mut l_x = limits.0 + width / 2.0;
        for _i in 0..n {
            sum += func.fun(l_x) * width;
            l_x += width;
        }
        return Ok(sum);
    }
}

impl IntegralSolver for MiddleRectangleSolver {
    type SolverOptions = ProperIntegralOptions;

    type Solution = ProperIntegralSolution;

    fn solve(&self, opts: Self::SolverOptions) -> Result<Self::Solution, Box<dyn Error>> {
        solve_with_runge(opts, Self::middle_rectangle, 2)
    }
}

impl Display for MiddleRectangleSolver {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Method of approximation by Middle-dominant Rectangles.")
    }
}

/*
Trapezoid
*/

pub struct TrapezoidSolver {}

impl TrapezoidSolver {
    fn trapezoid_solve(
        func: &Rc<Function>,
        limits: (f64, f64),
        n: u64,
    ) -> Result<f64, Box<dyn Error>> {
        let width = (limits.1 - limits.0) / n as f64;
        let mut sum = (func.fun(limits.0) + func.fun(limits.1)) / 2.0;
        let mut l_x = limits.0;
        for _i in 1..n {
            l_x += width;
            sum += func.fun(l_x);
        }
        return Ok(sum * width);
    }
}

impl IntegralSolver for TrapezoidSolver {
    type SolverOptions = ProperIntegralOptions;

    type Solution = ProperIntegralSolution;

    fn solve(&self, opts: Self::SolverOptions) -> Result<Self::Solution, Box<dyn Error>> {
        solve_with_runge(opts, Self::trapezoid_solve, 2)
    }
}

impl Display for TrapezoidSolver {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Method of approximation by Trapezoids.")
    }
}

/*
Simpson method
*/

pub struct SimpsonSolver {}

impl SimpsonSolver {
    fn simpson_solve(
        func: &Rc<Function>,
        limits: (f64, f64),
        n: u64,
    ) -> Result<f64, Box<dyn Error>> {
        let width = (limits.1 - limits.0) / n as f64;
        let mut sum = func.fun(limits.0) + func.fun(limits.1);
        let mut l_x = limits.0 + width;
        for i in 1..n {
            sum += func.fun(l_x) * (if i % 2 == 0 { 2.0 } else { 4.0 });
            l_x += width;
        }
        return Ok(sum * width / 3.0);
    }
}

impl IntegralSolver for SimpsonSolver {
    type SolverOptions = ProperIntegralOptions;

    type Solution = ProperIntegralSolution;

    fn solve(&self, opts: Self::SolverOptions) -> Result<Self::Solution, Box<dyn Error>> {
        solve_with_runge(opts, Self::simpson_solve, 4)
    }
}

impl Display for SimpsonSolver {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Simpson's method of approximation.")
    }
}
