use crate::function::Function;
use crate::misc::{get_table_str, tridiagonal_solve};
use std::{collections::VecDeque, error::Error, fmt::Display, iter::zip, rc::Rc};
pub trait InterpolationBuilder: Display {
    type InterpolationOptions;

    fn approximate(
        &self,
        opts: &Self::InterpolationOptions,
    ) -> Result<Box<dyn FunctionInterpolation>, Box<dyn Error>>;
}
pub trait FunctionInterpolation: Display + Send {
    fn fun(&self, arg: f64) -> f64;
    fn clone_dyn(&self) -> Box<dyn FunctionInterpolation>;
    fn legend_name(&self) -> String;
}
impl Clone for Box<dyn FunctionInterpolation> {
    fn clone(&self) -> Self {
        self.clone_dyn()
    }
}

pub struct RegularInterpolationOptions {
    points: Vec<(f64, f64)>,
    divided_differences: Vec<Vec<f64>>,
    finite_differences: Option<Vec<Vec<f64>>>,
    step: Option<f64>,
}

impl RegularInterpolationOptions {
    pub fn new(mut points: Vec<(f64, f64)>) -> RegularInterpolationOptions {
        if points.len() < 2 {
            panic!("Can't build interpolator without at least 2 points");
        }
        points.sort_by(|a, b| a.0.total_cmp(&b.0));
        let cur_step = points[1].0 - points[0].0;
        let mut step = Some(cur_step);
        for i in 1..(points.len() - 1) {
            if ((points[i + 1].0 - points[i].0) - cur_step).abs() > 0.0001 {
                println!("{} {} {}", points[i + 1].0, points[i].0, cur_step);
                step = None;
                break;
            }
        }
        let mut finite_differences: Option<Vec<Vec<f64>>> = None;
        if step.is_some() {
            let mut finite_differences_vec: Vec<Vec<f64>> = vec![];
            finite_differences_vec.push(points.iter().map(|a| a.1).collect());
            for i in 1..points.len() {
                let mut temp_vec = vec![];
                for j in 0..finite_differences_vec[i - 1].len() - 1 {
                    temp_vec.push(
                        finite_differences_vec[i - 1][j + 1] - finite_differences_vec[i - 1][j],
                    );
                }
                finite_differences_vec.push(temp_vec);
            }
            finite_differences = Some(finite_differences_vec);
        }

        let mut divided_differences: Vec<Vec<f64>> = vec![];
        divided_differences.push(points.iter().map(|a| a.1).collect());
        for i in 1..points.len() {
            let mut temp_vec = vec![];
            for j in 0..divided_differences[i - 1].len() - 1 {
                temp_vec.push(
                    (divided_differences[i - 1][j + 1] - divided_differences[i - 1][j])
                        / (points[j + i].0 - points[j].0),
                );
            }
            divided_differences.push(temp_vec);
        }

        RegularInterpolationOptions {
            points,
            finite_differences,
            divided_differences,
            step,
        }
    }
    pub fn is_regular_grid(&self) -> bool {
        self.finite_differences.is_some()
    }

    pub fn finite_differences(&self) -> Option<&Vec<Vec<f64>>> {
        self.finite_differences.as_ref()
    }
}

#[derive(Clone)]
struct LagrangeInterpolationFunction {
    coeffs: Vec<f64>,
    points_x: Vec<f64>,
}
impl LagrangeInterpolationFunction {
    pub fn new(coeffs: Vec<f64>, points_x: Vec<f64>) -> LagrangeInterpolationFunction {
        LagrangeInterpolationFunction { coeffs, points_x }
    }
}

impl Display for LagrangeInterpolationFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let coeff_header = (0..self.coeffs.len())
            .map(|el| format!("c_{el}"))
            .collect::<Vec<String>>();
        write!(
            f,
            "\
        ======================================\n\
        Lagrange Interpolation\n\
        --------------------------------------\n\
        {}\n\
        ",
            get_table_str(&coeff_header, vec![&self.coeffs], 6, 3)
        )
    }
}

impl FunctionInterpolation for LagrangeInterpolationFunction {
    fn fun(&self, arg: f64) -> f64 {
        let mut result = 0.0;
        for i in 0..self.coeffs.len() {
            let mut temp = self.coeffs[i];
            temp *= self.points_x[0..i]
                .iter()
                .cloned()
                .chain(self.points_x[i + 1..].iter().cloned())
                .fold(1.0, |acc, el| acc * (arg - el));
            result += temp;
        }
        return result;
    }
    fn clone_dyn(&self) -> Box<dyn FunctionInterpolation> {
        Box::new(self.clone())
    }

    fn legend_name(&self) -> String {
        return format!("Lagrange interpolation");
    }
}

pub struct LagrangeInterpolation {}

impl Display for LagrangeInterpolation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Lagrange interpolation")
    }
}
impl InterpolationBuilder for LagrangeInterpolation {
    type InterpolationOptions = RegularInterpolationOptions;

    fn approximate(
        &self,
        opts: &Self::InterpolationOptions,
    ) -> Result<Box<dyn FunctionInterpolation>, Box<dyn Error>> {
        let mut coeffs = vec![];
        let points_x: Vec<f64> = opts.points.iter().map(|el| el.0).collect();
        for i in 0..opts.points.len() {
            coeffs.push(opts.points[i].1);
            coeffs[i] /= points_x[0..i]
                .iter()
                .cloned()
                .chain(points_x[i + 1..].iter().cloned())
                .fold(1.0, |acc, el| acc * (points_x[i] - el))
        }

        Ok(Box::new(LagrangeInterpolationFunction::new(
            coeffs, points_x,
        )))
    }
}

#[derive(Clone)]
struct NewtonIrregularGridFunction {
    coeffs: Vec<f64>,
    points_x: Vec<f64>,
}
impl NewtonIrregularGridFunction {
    pub fn new(coeffs: Vec<f64>, points_x: Vec<f64>) -> NewtonIrregularGridFunction {
        NewtonIrregularGridFunction { coeffs, points_x }
    }
}

impl Display for NewtonIrregularGridFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let coeff_header = (0..self.coeffs.len())
            .map(|el| format!("f_{el}"))
            .collect::<Vec<String>>();
        write!(
            f,
            "\
        ======================================\n\
        Newton Irregular Grid Interpolation\n\
        --------------------------------------\n\
        {}\n\
        ",
            get_table_str(&coeff_header, vec![&self.coeffs], 6, 3)
        )
    }
}

impl FunctionInterpolation for NewtonIrregularGridFunction {
    fn fun(&self, arg: f64) -> f64 {
        let mut result = 0.0;
        for i in 0..self.coeffs.len() {
            let mut temp = self.coeffs[i];
            temp *= self.points_x[0..i]
                .iter()
                .fold(1.0, |acc, el| acc * (arg - el));
            result += temp;
        }
        return result;
    }
    fn clone_dyn(&self) -> Box<dyn FunctionInterpolation> {
        Box::new(self.clone())
    }

    fn legend_name(&self) -> String {
        return format!("Newton irreg. grid interpolation");
    }
}

pub struct NewtonIrregularGridInterpolation {}

impl Display for NewtonIrregularGridInterpolation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Newton Irregular Grid interpolation")
    }
}
impl InterpolationBuilder for NewtonIrregularGridInterpolation {
    type InterpolationOptions = RegularInterpolationOptions;

    fn approximate(
        &self,
        opts: &Self::InterpolationOptions,
    ) -> Result<Box<dyn FunctionInterpolation>, Box<dyn Error>> {
        let coeffs = opts.divided_differences.iter().map(|el| el[0]).collect();
        let points_x: Vec<f64> = opts.points.iter().map(|el| el.0).collect();
        Ok(Box::new(NewtonIrregularGridFunction::new(coeffs, points_x)))
    }
}

#[derive(Clone)]
struct NewtonRegularGridFunction {
    forward_coeffs: Vec<f64>,
    back_coeffs: Vec<f64>,
    mid_point: f64,
    points_x: Vec<f64>,
    step: f64,
}
impl NewtonRegularGridFunction {
    pub fn new(
        forward_coeffs: Vec<f64>,
        back_coeffs: Vec<f64>,
        points_x: Vec<f64>,
        mid_point: f64,
        step: f64,
    ) -> NewtonRegularGridFunction {
        NewtonRegularGridFunction {
            forward_coeffs,
            back_coeffs,
            mid_point,
            step,
            points_x,
        }
    }
}

impl Display for NewtonRegularGridFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let forward_coeff_header = (0..self.forward_coeffs.len())
            .map(|el| format!("f_{el}"))
            .collect::<Vec<String>>();
        let back_coeff_header = (0..self.back_coeffs.len())
            .map(|el| format!("b_{el}"))
            .collect::<Vec<String>>();
        write!(
            f,
            "\
        ======================================\n\
        Newton Regular Grid Interpolation\n\
        --------------------------------------\n\
        {}\n{}\n\
        Mid point: {}\n\
        Step: {}\n\
        ",
            get_table_str(&forward_coeff_header, vec![&self.forward_coeffs], 6, 3),
            get_table_str(&back_coeff_header, vec![&self.back_coeffs], 6, 3),
            self.mid_point,
            self.step
        )
    }
}

impl FunctionInterpolation for NewtonRegularGridFunction {
    fn fun(&self, arg: f64) -> f64 {
        let mut result = 0.0;
        if arg > self.mid_point {
            let mut closest_bound = self.points_x.len() - 1;
            for i in self.points_x.len() - 1..=0 {
                if arg < self.points_x[i] {
                    closest_bound = i;
                    break;
                }
            }
            let t = (arg - self.points_x[closest_bound]) / self.step;
            for i in (self.points_x.len() - closest_bound - 1)..self.back_coeffs.len() {
                let mut temp = self.back_coeffs[i];
                temp *= (0..i).fold(1.0, |acc, el| acc * (t + el as f64));
                result += temp / ((1..=i).product::<usize>() as f64);
            }
        } else {
            let mut closest_bound = 0;
            for i in 0..self.points_x.len() {
                if arg > self.points_x[i] {
                    closest_bound = i;
                    break;
                }
            }
            let t = (arg - self.points_x[closest_bound]) / self.step;
            for i in closest_bound..self.forward_coeffs.len() {
                let mut temp = self.forward_coeffs[i];
                temp *= (0..i).fold(1.0, |acc, el| acc * (t - el as f64));
                result += temp / ((1..=i).product::<usize>() as f64);
            }
        }

        return result;
    }
    fn clone_dyn(&self) -> Box<dyn FunctionInterpolation> {
        Box::new(self.clone())
    }

    fn legend_name(&self) -> String {
        return format!("Newton reg. grid interpolation");
    }
}

pub struct NewtonRegularGridInterpolation {}

impl Display for NewtonRegularGridInterpolation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Newton Regular Grid interpolation")
    }
}
impl InterpolationBuilder for NewtonRegularGridInterpolation {
    type InterpolationOptions = RegularInterpolationOptions;

    fn approximate(
        &self,
        opts: &Self::InterpolationOptions,
    ) -> Result<Box<dyn FunctionInterpolation>, Box<dyn Error>> {
        if opts.finite_differences.is_none() || opts.step.is_none() {
            panic!("Provided grid is not regular");
        }
        let finite_diff = opts.finite_differences.as_ref().unwrap();
        let forward_coeff = finite_diff.iter().map(|el| el[0]).collect();
        let back_coeff = finite_diff
            .iter()
            .map(|el| el.last().unwrap().clone())
            .collect();
        let points_x: Vec<f64> = opts.points.iter().map(|el| el.0).collect();
        let mid_point = points_x[(points_x.len() - 1) / 2];
        Ok(Box::new(NewtonRegularGridFunction::new(
            forward_coeff,
            back_coeff,
            points_x,
            mid_point,
            opts.step.unwrap(),
        )))
    }
}

#[derive(Clone)]
struct CubicSplineFunction {
    coeffs: Vec<[f64; 4]>,
    points_x: Vec<f64>,
}
impl CubicSplineFunction {
    pub fn new(coeffs: Vec<[f64; 4]>, points_x: Vec<f64>) -> CubicSplineFunction {
        CubicSplineFunction { coeffs, points_x }
    }
}

impl Display for CubicSplineFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let header = vec!["a_i", "b_i", "c_i", "d_i"];
        let a: Vec<Vec<f64>> = self.coeffs.iter().map(|el| el.to_vec()).collect();
        write!(
            f,
            "\
        ======================================\n\
        Cubic Spline Interpolation\n\
        --------------------------------------\n\
        {}\n\
        ",
            get_table_str(&header, a.iter().collect(), 6, 3),
        )
    }
}

impl FunctionInterpolation for CubicSplineFunction {
    fn fun(&self, arg: f64) -> f64 {
        let mut interval = self.coeffs.len() - 1;
        for i in 1..self.points_x.len() {
            if arg < self.points_x[i] {
                interval = i - 1;
                break;
            }
        }
        let mut result = 0.0;
        for i in 0..4 {
            result += self.coeffs[interval][i] * (arg - self.points_x[interval]).powi(i as i32);
        }
        return result;
    }
    fn clone_dyn(&self) -> Box<dyn FunctionInterpolation> {
        Box::new(self.clone())
    }

    fn legend_name(&self) -> String {
        return format!("Cubic Spline interpolation");
    }
}

pub struct CubicSplineInterpolation {}

impl Display for CubicSplineInterpolation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Cubic Spline interpolation")
    }
}
impl InterpolationBuilder for CubicSplineInterpolation {
    type InterpolationOptions = RegularInterpolationOptions;

    fn approximate(
        &self,
        opts: &Self::InterpolationOptions,
    ) -> Result<Box<dyn FunctionInterpolation>, Box<dyn Error>> {
        let points_x: Vec<f64> = opts.points.iter().map(|el| el.0).collect();
        let mut steps = vec![];
        for i in 0..points_x.len() - 1 {
            steps.push(points_x[i + 1] - points_x[i]);
        }
        let mut lower_diag = vec![0.0; steps.len() - 1];
        lower_diag.copy_from_slice(&steps[..steps.len() - 1]);
        lower_diag.push(0.0);

        let mut mid_diag = vec![1.0];
        for i in 0..steps.len() - 1 {
            mid_diag.push(2.0 * (steps[i] + steps[i + 1]));
        }
        mid_diag.push(1.0);

        let mut upper_diag = vec![0.0];
        upper_diag.extend_from_slice(&steps[1..]);

        let mut res_vec = vec![0.0];
        for i in 0..steps.len() - 1 {
            res_vec.push(
                3.0 * ((opts.points[i + 2].1 - opts.points[i + 1].1) / steps[i + 1]
                    - (opts.points[i + 1].1 - opts.points[i].1) / steps[i]),
            );
        }
        res_vec.push(0.0);

        let c = tridiagonal_solve(lower_diag, mid_diag, upper_diag, res_vec);
        let a: Vec<f64> = opts.points.iter().map(|el| el.1).collect();

        let mut b = vec![];
        let mut d = vec![];
        for i in 0..a.len() - 1 {
            b.push((a[i + 1] - a[i]) / steps[i] - (2.0 * c[i] + c[i + 1]) / 3.0 * steps[i]);
            d.push((c[i + 1] - c[i]) / (3.0 * steps[i]));
        }

        let coeffs: Vec<[f64; 4]> = zip(a, zip(b, zip(c, d)))
            .map(|el| [el.0, el.1 .0, el.1 .1 .0, el.1 .1 .1])
            .collect();
        Ok(Box::new(CubicSplineFunction::new(coeffs, points_x)))
    }
}

#[derive(Clone)]
pub struct RealFunction {
    function: Function,
}
impl RealFunction {
    pub fn new(function: Function) -> RealFunction {
        RealFunction { function }
    }
}
impl Display for RealFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "",)
    }
}

impl FunctionInterpolation for RealFunction {
    fn fun(&self, arg: f64) -> f64 {
        return self.function.fun(arg);
    }
    fn clone_dyn(&self) -> Box<dyn FunctionInterpolation> {
        Box::new(self.clone())
    }

    fn legend_name(&self) -> String {
        return format!("Real Function");
    }
}
