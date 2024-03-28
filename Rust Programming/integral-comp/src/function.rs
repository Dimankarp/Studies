pub struct Function {
    fun: fn(f64) -> f64,
    display_form: &'static str,
    continuity_fn: Option<fn((f64, f64)) -> bool>,
    antiderivative: Option<fn(f64) -> f64>,
}
const DISCRETE: u16 = 1000;
/*
    Absolute value of the derivative above which the value of
    the function is considered infinity.

*/
const INF_DERIVATIVE_VALUE: f64 = 1e5;
const CRITICAL_POINT_PRECISION: f64 = 1e-5;
impl Function {
    pub fn new(
        fun: fn(f64) -> f64,
        display_form: &'static str,
        continuity_fn: Option<fn((f64, f64)) -> bool>,
        antiderivative: Option<fn(f64) -> f64>,
    ) -> Self {
        Function {
            fun,
            display_form,
            continuity_fn,
            antiderivative,
        }
    }

    pub fn fun(&self, var: f64) -> f64 {
        return (self.fun)(var);
    }

    pub fn is_full_continuous(&self) -> bool {
        match self.continuity_fn {
            Some(_) => false,
            None => true,
        }
    }

    pub fn try_integrating(&self, limits: (f64, f64)) -> Option<f64> {
        match self.antiderivative {
            Some(anti) => Some(anti(limits.1) - anti(limits.0)),
            None => None,
        }
    }

    pub fn continuity_check(&self, limits: (f64, f64)) -> bool {
        if limits.0 > limits.1 {
            panic!("Provided limits are incorrect: {:?}.", limits)
        }
        match self.continuity_fn {
            Some(ref fun) => {
                return fun(limits);
            }
            None => true,
        }
    }

    pub fn has_critical_points(&self, limits: (f64, f64)) -> Option<f64> {
        let h = (limits.1 - limits.0) / (DISCRETE as f64);
        let prev_f = (self.fun)(limits.0);
        if prev_f.is_infinite() {
            return Some(limits.0);
        }
        for i in 1..DISCRETE {
            let next_f = (self.fun)(limits.0 + i as f64 * h);
            if next_f.is_infinite() {
                return Some(limits.0 + i as f64 * h);
            }
            let diff = (next_f - prev_f).abs();
            let deriv = diff / h;
            if deriv > INF_DERIVATIVE_VALUE {
                return Some(limits.0 + (i - 1) as f64 * h + h / 2.0);
            }
        }
        None
    }
    /*
    This method is based on the fact that for functions like 1/x or 1/(x^3+2x^2+x) (which basically are the functions that we're
    intereseted to find critical points for) if ratio of f(x)/f'(x) tends to zero at x_0 than x_0 might be critical.

    See example:

    f(x) = x/(x^3+2x^2+x) f'(x) = (2x+2)/(x^2+2x+1)^2 and f(x)/f'(x)= (x^2+2x+1)/(2x+2) at x=-1 tends to zero
    and x_0=-1 is a critical points.

    There excpetions, though (that's why this method has suffix - experimental). See for example function f(x)=sin(x)
     */
    pub fn has_critical_points_experimental(&self, limits: (f64, f64)) -> Option<f64> {
        let h = (limits.1 - limits.0) / (DISCRETE as f64);
        let prev_f = (self.fun)(limits.0);
        if prev_f.is_infinite() {
            return Some(limits.0);
        }
        for i in 1..DISCRETE {
            let next_f = (self.fun)(limits.0 + i as f64 * h);
            if next_f.is_infinite() {
                return Some(limits.0 + i as f64 * h);
            }
            let diff = (next_f - prev_f).abs();
            let deriv = diff / h;

            let mid_point = limits.0 + (i - 1) as f64 * h + h / 2.0;
            let f_mid_point = (self.fun)(mid_point);
            if (f_mid_point / deriv).abs() < CRITICAL_POINT_PRECISION {
                return Some(mid_point);
            }
        }
        None
    }
}

impl std::fmt::Display for Function {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.display_form)
    }
}
