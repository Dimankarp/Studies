pub struct Function {
    fun: fn(&[f64]) -> f64,
    derivs: Vec<fn(&[f64]) -> f64>,
    derivs_second: Option<Vec<fn(&[f64]) -> f64>>,
    display_form: &'static str,
    vars_count: usize,
}

impl Function {
    pub fn new(
        vars_count: usize,
        fun: fn(&[f64]) -> f64,
        derivs: Vec<fn(&[f64]) -> f64>,
        derivs_second: Option<Vec<fn(&[f64]) -> f64>>,
        display_form: &'static str,
    ) -> Self {
        if derivs.len() != vars_count {
            panic!("Number of derivs in a provided array is not equal to number of vars.")
        }
        Function {
            vars_count,
            fun,
            derivs,
            display_form,
            derivs_second
        }
    }

    pub fn fun(&self, vars: &[f64]) -> f64 {
        if vars.len() != self.vars_count.into() {
            panic!(
                "Provided invalid number of vars {} to a {}-size equation.",
                vars.len(),
                self.vars_count
            )
        }
        return (self.fun)(vars);
    }

    pub fn deriv_closures(&self) -> &Vec<fn(&[f64]) -> f64> {
        &self.derivs
    }

    pub fn fun_closure(&self) ->  fn(&[f64]) -> f64 {
        self.fun
    }


    pub fn deriv(&self, vars: &[f64], deriv: usize) -> f64 {
        if vars.len() != self.vars_count.into() {
            panic!(
                "Provided invalid number of vars {} to a {}-size equation.",
                vars.len(),
                self.vars_count
            )
        }
        if self.derivs.len() <= deriv {
            panic!(
                "Provided invalid  derivative index {} to a {}-size equation.",
                deriv, self.vars_count
            )
        }
        return self
            .derivs
            .get(deriv)
            .expect("The check for number of derivatives was done!")(vars);
    }

    pub fn derivs_second(&self) -> &Option<Vec<fn(&[f64]) -> f64>> {
         &self.derivs_second
    }

    pub fn vars_count(&self) -> usize{
        self.vars_count
    }
}

impl std::fmt::Display for Function {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.display_form)
    }
}
