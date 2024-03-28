use std::error::Error;

use crate::{
    equation::Equation, function::Function, EquationSolution, MethodOptions, Solution, System,
    SystemSolution,
};

const DEFAULT_ROOT_CHECK_STEP: f64 = 0.2;
const MAX_POINTS_CHECK: u8 = 30;

pub fn bisection_solve<'a, 'b>(
    eq: &'a Equation,
    opts: &'b MethodOptions,
) -> Result<Solution, Box<dyn Error>> {
    let opts = match opts {
        MethodOptions::EquationMethodOptions(eq) => eq,
        MethodOptions::SystemMethodOptions(_) => {
            panic!("This method is for equation solving only!")
        }
    };
    eprintln!("Trying to solve {} with Bisection method", eq);
    if eq.fun().vars_count() != 1 {
        panic!("Bisection solving is only applicable to single variable function")
    }

    err_if_not_single_root(eq.fun(), opts.root_interval, DEFAULT_ROOT_CHECK_STEP)?;

    let mut a = opts.root_interval.0;
    let mut b = opts.root_interval.1;

    let mut f_a = eq.fun().fun(&[a]);

    let mut last_x = a;
    let mut last_f_x = f_a;

    let mut iterations = 0;
    Ok(loop {
        iterations += 1;
        let x = (a + b) / 2.0;
        let f_x = eq.fun().fun(&[x]);

        /*  eprintln!(
            "{}| x={}, f_x={}, |x-last_x|={}, |f_x - f_last_x|={}",
            iterations,
            x,
            f_x,
            (x - last_x).abs(),
            (f_x - last_f_x).abs()
        );*/
        match opts.term_criteria {
            crate::EquationTerminationCriteria::ByArgumentDiff => {
                if (x - last_x).abs() < opts.precision {
                    break Solution::EquationSolution(EquationSolution {
                        x,
                        f_x,
                        iterations,
                        equation: eq.clone(),
                        root_interval: opts.root_interval,
                    });
                };
            }
            crate::EquationTerminationCriteria::ByFunctionDiff => {
                if (f_x - last_f_x).abs() < opts.precision {
                    break Solution::EquationSolution(EquationSolution {
                        x,
                        f_x,
                        iterations,
                        equation: eq.clone(),
                        root_interval: opts.root_interval,
                    });
                };
            }
        }

        if iterations >= 100 {
            return Err(format!("Couldn't meet precision in 100 iterations! Aborting").into());
        }

        last_x = x;
        last_f_x = f_x;

        if f_a * f_x < 0.0 {
            b = x;
        } else {
            a = x;
            f_a = f_x;
        }
    })
}

pub fn secant_solve<'a, 'b>(
    eq: &'a Equation,
    opts: &'b MethodOptions,
) -> Result<Solution, Box<dyn Error>> {
    let opts = match opts {
        MethodOptions::EquationMethodOptions(eq) => eq,
        MethodOptions::SystemMethodOptions(_) => {
            panic!("This method is for equation solving only!")
        }
    };
    eprintln!("Trying to solve {} with Secant method", eq);
    if eq.fun().vars_count() != 1 {
        panic!("Secant solving is only applicable to single variable function")
    }

    let derivs_second = match eq.fun().derivs_second() {
        Some(derivs) => derivs,
        None => {
            return Err(format!(
                "Method of secants requires second derivatives to function properly"
            )
            .into())
        }
    };

    err_if_not_single_root(eq.fun(), opts.root_interval, DEFAULT_ROOT_CHECK_STEP)?;

    if !is_same_sign_on_interval(
        derivs_second[0],
        opts.root_interval,
        DEFAULT_ROOT_CHECK_STEP,
    ) {
        eprintln!("WARNING! Failed to prove that Method of Secants for chosen function on provided interval converges. Though continue execution")
    }

    let mut x_prev;
    match opts.root_approx {
        Some(approx) => x_prev = approx,
        None => {
            if eq.fun().fun(&[opts.root_interval.0]) * derivs_second[0](&[opts.root_interval.0])
                > 0.0
            {
                x_prev = opts.root_interval.0;
            } else {
                x_prev = opts.root_interval.1;
            }
        }
    }

    let mut x_mid = (opts.root_interval.0 + opts.root_interval.1) / 2.0;

    eprintln!("Starting values: x_0 ={} x_1={}", x_prev, x_mid);

    let mut last_f_x = eq.fun().fun(&[x_mid]);
    let mut prev_f_x = eq.fun().fun(&[x_prev]);

    let mut iterations = 0;
    Ok(loop {
        iterations += 1;
        let x = x_mid - (x_mid - x_prev) / (last_f_x - prev_f_x) * last_f_x;
        let f_x = eq.fun().fun(&[x]);

        // eprintln!(
        //     "{}| x={}, f_x={}, |x-last_x|={}, |f_x-f_last_x|={}",
        //     iterations,
        //     x,
        //     f_x,
        //     (x - x_mid).abs(),
        //     (f_x - last_f_x).abs()
        // );
        match opts.term_criteria {
            crate::EquationTerminationCriteria::ByArgumentDiff => {
                if (x - x_mid).abs() < opts.precision {
                    break Solution::EquationSolution(EquationSolution {
                        x,
                        f_x,
                        iterations,
                        equation: eq.clone(),
                        root_interval: opts.root_interval,
                    });
                };
            }
            crate::EquationTerminationCriteria::ByFunctionDiff => {
                if (f_x - last_f_x).abs() < opts.precision {
                    break Solution::EquationSolution(EquationSolution {
                        x,
                        f_x,
                        iterations,
                        equation: eq.clone(),
                        root_interval: opts.root_interval,
                    });
                };
            }
        }

        if iterations >= 100 {
            return Err(format!("Couldn't meet precision in 100 iterations! Aborting").into());
        }

        x_prev = x_mid;
        prev_f_x = last_f_x;

        x_mid = x;
        last_f_x = f_x;
    })
}

pub fn iterative_solve<'a, 'b>(
    eq: &'a Equation,
    opts: &'b MethodOptions,
) -> Result<Solution, Box<dyn Error>> {
    let opts = match opts {
        MethodOptions::EquationMethodOptions(eq) => eq,
        MethodOptions::SystemMethodOptions(_) => {
            panic!("This method is for equation solving only!")
        }
    };
    eprintln!("Trying to solve {} with an Iterative method", eq);
    if eq.fun().vars_count() != 1 {
        panic!("Iterative solving is only applicable to single variable function")
    }
    eprintln!("WARNING! This implementation will try to find maximum of the first derivative to make method converge. Since method of finding this maximum is quite rough - expect that method might diverge");

    err_if_not_single_root(eq.fun(), opts.root_interval, DEFAULT_ROOT_CHECK_STEP)?;

    if !is_same_sign_on_interval(
        eq.fun().deriv_closures()[0],
        opts.root_interval,
        (opts.root_interval.1 - opts.root_interval.0) / MAX_POINTS_CHECK as f64,
    ) {
        eprintln!("WARNING! First derivative changes sign on the provided interval - there is a bigger chance that series will diverge!");
    }

    let max_by_abs = try_rough_abs_maximizing(
        eq.fun().deriv_closures()[0],
        opts.root_interval,
        (opts.root_interval.1 - opts.root_interval.0) / MAX_POINTS_CHECK as f64,
    );

    let lambda = -1.0 / max_by_abs;
    eprintln!("Rough max|f'(x)| ={}, lambda={}", max_by_abs.abs(), lambda);

    eprintln!(
        "phi'(a) = {}",
        1.0 + lambda * eq.fun().deriv_closures()[0](&[opts.root_interval.0])
    );
    eprintln!(
        "f'(b) = {}",
        eq.fun().deriv_closures()[0](&[opts.root_interval.1])
    );
    eprintln!(
        "phi'(b) = {}",
        1.0 + lambda * eq.fun().deriv_closures()[0](&[opts.root_interval.1])
    );
    let mut x_prev = match opts.root_approx {
        Some(approx) => approx,
        None => (opts.root_interval.0 + opts.root_interval.1) / 2.0,
    };
    let mut f_prev = eq.fun().fun(&[x_prev]);

    eprintln!("Starting values: x_0 ={}", x_prev);

    let mut iterations = 0;
    Ok(loop {
        iterations += 1;
        let x = x_prev + lambda * f_prev;
        let f_x = eq.fun().fun(&[x]);

        // eprintln!(
        //     "{}| x={}, f_x={}, |x-last_x|={}, |f_x-last_f_x|={}",
        //     iterations,
        //     x,
        //     f_x,
        //     (x - x_prev).abs(),
        //     (f_x - f_prev).abs()
        // );
        match opts.term_criteria {
            crate::EquationTerminationCriteria::ByArgumentDiff => {
                if (x - x_prev).abs() < opts.precision {
                    break Solution::EquationSolution(EquationSolution {
                        x,
                        f_x,
                        iterations,
                        equation: eq.clone(),
                        root_interval: opts.root_interval,
                    });
                };
            }
            crate::EquationTerminationCriteria::ByFunctionDiff => {
                if (f_x - f_prev).abs() < opts.precision {
                    break Solution::EquationSolution(EquationSolution {
                        x,
                        f_x,
                        iterations,
                        equation: eq.clone(),
                        root_interval: opts.root_interval,
                    });
                };
            }
        }

        if iterations >= 100 {
            return Err(format!("Couldn't meet precision in 100 iterations! Aborting").into());
        }

        x_prev = x;
        f_prev = f_x;
    })
}

pub fn newton_solve<'a, 'b>(
    system: &'a System,
    opts: &'b MethodOptions,
) -> Result<Solution, Box<dyn Error>> {
    let opts = match opts {
        MethodOptions::EquationMethodOptions(_) => {
            panic!("This method is for system solving only!")
        }
        MethodOptions::SystemMethodOptions(opts) => opts,
    };
    if system.size() != 2 {
        panic!("This method is for 2 variable systems only!")
    }
    let mut last_x_vec = opts.root_approxs.clone();
    let mut iterations = 0;
    loop {
        iterations += 1;
        let fun_1 = system.equations()[0].fun().fun(&last_x_vec);
        let fun_1_der_x = system.equations()[0].fun().deriv(&last_x_vec, 0);
        let fun_1_der_y = system.equations()[0].fun().deriv(&last_x_vec, 1);

        let fun_2 = system.equations()[1].fun().fun(&last_x_vec);
        let fun_2_der_x = system.equations()[1].fun().deriv(&last_x_vec, 0);
        let fun_2_der_y = system.equations()[1].fun().deriv(&last_x_vec, 1);

        println!("f1 {fun_1}, f1_der_x {fun_1_der_x}, fun1_der_y {fun_1_der_y}");
        println!("f2 {fun_2}, f2_der_x {fun_2_der_x}, fun2_der_y {fun_2_der_y}");
        let delta_y;
        let delta_x;
        //Fighting zeroes in derivatives
        if fun_1_der_x.abs() < 0.000001 {
            if fun_2_der_x.abs() < 0.000001 {
                return Err(format!("In an unfortunate turn of events - the iterating matrix is unsolvable as fun_1_der_x={fun_1_der_x} and fun_2_der_x={fun_2_der_x} are near to zero! Aborting computation").into());
            } else {
                delta_y = (-fun_1) / (fun_1_der_y);
                delta_x = (-fun_2 - fun_2_der_y * delta_y) / fun_2_der_x;
            }
        } else {
            delta_y = (-fun_2) / (fun_2_der_y - (fun_1_der_y * fun_2_der_x / fun_1_der_x));
            delta_x = (-fun_1 - fun_1_der_y * delta_y) / fun_1_der_x;
        }

        let mut new_x_vec = last_x_vec.clone();
        new_x_vec[0] += delta_x;
        new_x_vec[1] += delta_y;

        eprintln!(
            "{}| x={}, y={}, f_1={}, f_2={} |x-last_x|={}, |y-last_y|={}",
            iterations,
            new_x_vec[0],
            new_x_vec[1],
            system.equations()[0].fun().fun(&new_x_vec),
            system.equations()[1].fun().fun(&new_x_vec),
            delta_x.abs(),
            delta_y.abs()
        );

        if (new_x_vec[0] - last_x_vec[0]).abs() < opts.precision
            && (new_x_vec[1] - last_x_vec[1]).abs() < opts.precision
        {
            let mut diff_vec = vec![];
            for (i, new_x) in new_x_vec.iter().enumerate() {
                diff_vec.push(new_x - last_x_vec[i]);
            }
            break Ok(Solution::SystemSolution(SystemSolution {
                x_vec: new_x_vec,
                iterations,
                x_approx_vec: opts.root_approxs.clone(),
                system: system.clone(),
                x_diff_vec: diff_vec,
            }));
        }

        if iterations >= 100 {
            return Err(format!("Couldn't meet precision in 100 iterations! Aborting").into());
        }
        last_x_vec = new_x_vec;
    }
}

pub fn count_single_func_roots(func: &Function, interval: (f64, f64), step: f64) -> i32 {
    if func.vars_count() != 1 {
        panic!("This method is implemented only for single variable fucntions.");
    }
    if interval.0 > interval.1 {
        panic!("Invalid interval!")
    }
    if step <= 0.0 {
        panic!("Step must be a positive float");
    }
    let mut count = 0;
    let mut prev_x = interval.0;
    let mut prev_f = func.fun(&[interval.0]);
    loop {
        let next_x = (prev_x + step).min(interval.1);
        let next_f = func.fun(&[next_x]);
        if prev_f * next_f < 0.0 || (next_f == 0.0 && prev_f != next_f) {
            //Yes, exact zero!
            count += 1;
        }

        if next_x >= interval.1 {
            break;
        }
        prev_x = next_x;
        prev_f = next_f;
    }
    count
}

fn is_same_sign_on_interval(
    func_closure: fn(&[f64]) -> f64,
    interval: (f64, f64),
    step: f64,
) -> bool {
    return count_single_func_roots(
        &Function::new(1, func_closure, vec![|_x| 0.0], None, "CLOSURE WRAPPER"),
        interval,
        step,
    ) == 0;
}

fn err_if_not_single_root(
    func: &Function,
    interval: (f64, f64),
    step: f64,
) -> Result<bool, Box<dyn Error>> {
    let root_count = count_single_func_roots(func, interval, step);
    match root_count {
        0 => {
            return Err(format!(
            "Didn't manage to locate any roots on the provided interval for the chosen function."
        )
            .into())
        }
        1 => (),
        _ => {
            return Err(format!(
                "There are multiple roots on the provided interval for the chosen function."
            )
            .into());
        }
    }
    return Ok(true);
}

fn try_rough_abs_maximizing(
    func_closure: fn(&[f64]) -> f64,
    interval: (f64, f64),
    step: f64,
) -> f64 {
    if interval.0 > interval.1 {
        panic!("Invalid interval!")
    }
    if step <= 0.0 {
        panic!("Step must be a positive float");
    }
    let mut max_by_abs: f64 = 0.0;
    let mut prev_x = interval.0;
    loop {
        let next_x = (prev_x + step).min(interval.1);
        let next_f = func_closure(&[next_x]);

        if next_f.abs() > max_by_abs.abs() {
            max_by_abs = next_f
        }

        if next_x >= interval.1 {
            break;
        }
        prev_x = next_x;
    }
    max_by_abs
}

#[cfg(test)]
mod tests {

    use super::*;
    #[test]
    fn zero_roots() {
        let func = Function::new(1, |x| x[0] + 2.0, vec![|_x| 1.0], None, "TEST");
        assert_eq!(count_single_func_roots(&func, (-30.0, -20.0), 1.0), 0);
    }
    #[test]
    fn zero_roots_close_interval() {
        let func = Function::new(1, |x| x[0] + 2.0, vec![|_x| 1.0], None, "TEST");
        assert_eq!(count_single_func_roots(&func, (-30.0, -30.0), 1.0), 0);
    }

    #[test]
    fn single_root() {
        let func = Function::new(1, |x| x[0] + 2.0, vec![|_x| 1.0], None, "TEST");
        assert_eq!(count_single_func_roots(&func, (-3.0, 1.0), 0.2), 1);
    }

    #[test]
    fn single_root_2() {
        let func = Function::new(1, |x| 1.0 + 2.0 * x[0] - 3.0, vec![|_x| 1.0], None, "TEST");
        assert_eq!(count_single_func_roots(&func, (-10.0, 20.0), 0.5), 1);
    }

    #[test]
    fn triple_roots() {
        let func = Function::new(1, |x| x[0].sin(), vec![|_x| 1.0], None, "TEST");
        assert_eq!(count_single_func_roots(&func, (-1.0, 7.0), 0.2), 3);
    }
}
