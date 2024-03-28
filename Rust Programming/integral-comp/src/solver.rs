use std::{error::Error, fmt::Display};

pub trait IntegralSolver: Display {
    type SolverOptions;
    type Solution: Display;

    fn solve(&self, opts: Self::SolverOptions) -> Result<Self::Solution, Box<dyn Error>>;
}
