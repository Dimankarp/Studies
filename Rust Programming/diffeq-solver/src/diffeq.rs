use crate::function::{Function2Variables};

#[derive(Clone)]
pub struct DifferentialEquation {
    function: Function2Variables,
    solution: Function2Variables,
    c_func: fn((f64,f64))->f64, //Calculates constant c1
}

impl DifferentialEquation {
    pub fn new(function: Function2Variables, solution: Function2Variables, c_func: fn((f64,f64))->f64) -> Self {
        DifferentialEquation { function, solution, c_func }
    }

    pub fn func(&self) -> &Function2Variables {
        &self.function
    }

    pub fn solution(&self) -> &Function2Variables {
        &self.solution
    }

    pub fn constant(&self, initial_point: (f64,f64)) -> f64 {
        (self.c_func)(initial_point)
    }
}
