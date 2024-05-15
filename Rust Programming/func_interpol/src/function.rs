#[derive(Clone)]
pub struct Function {
    fun: fn(f64) -> f64,
    display_form: &'static str,
}

impl Function {
    pub fn new(fun: fn(f64) -> f64, display_form: &'static str) -> Self {
        Function { fun, display_form }
    }

    pub fn fun(&self, var: f64) -> f64 {
        return (self.fun)(var);
    }
}
impl std::fmt::Display for Function {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.display_form)
    }
}
