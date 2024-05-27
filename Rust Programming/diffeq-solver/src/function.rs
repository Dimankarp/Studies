
#[derive(Clone)]
pub struct Function2Variables {
    fun: fn(f64, f64) -> f64,
    display_form: &'static str,
}

impl Function2Variables {
    pub fn new(fun: fn(f64, f64) -> f64, display_form: &'static str) -> Self {
        Function2Variables { fun, display_form }
    }

    pub fn fun(&self, x: f64, y: f64) -> f64 {
        return (self.fun)(x, y);
    }

    pub fn fun_closure(&self) -> fn(f64, f64) -> f64{
        return self.fun
    }
}
impl std::fmt::Display for Function2Variables {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.display_form)
    }
}
