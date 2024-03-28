use std::rc::Rc;

use crate::function::Function;

pub struct Equation {
    fun: Rc<Function>,
    equal_to: f64,
}

impl Equation {
    pub const fn new(fun: Rc<Function>, equal_to: f64) -> Self {
        Equation { fun, equal_to }
    }
    pub fn fun(&self) -> &Function {
        &self.fun
    }
    pub fn equal_to(&self) -> f64 {
        self.equal_to
    }
}
impl<'a> std::fmt::Display for Equation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}={}", self.fun, self.equal_to)
    }
}

impl Clone for Equation {
    fn clone(&self) -> Self {
        Self {
            fun: self.fun.clone(),
            equal_to: self.equal_to.clone(),
        }
    }
}
