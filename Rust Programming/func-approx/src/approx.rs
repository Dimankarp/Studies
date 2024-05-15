use std::{error::Error, fmt::Display};

use crate::misc::gauss_solve;

pub trait ApproximationBuilder: Display {
    type ApproximationOptions;

    fn approximate(
        &self,
        opts: &Self::ApproximationOptions,
    ) -> Result<Box<dyn FunctionApproximation>, Box<dyn Error>>;
}

pub trait FunctionApproximation: Display {
    fn fun(&self, arg: f64) -> f64;
    fn clone_dyn(&self) -> Box<dyn FunctionApproximation>;
    fn legend_name(&self) -> String;
    fn sq_deviation(&self) -> f64;
    fn determ_coeff(&self) -> f64;
    fn deviaton_vec(&self) -> &Vec<f64>;
    fn approx_vec(&self) -> &Vec<f64>;
}
impl Clone for Box<dyn FunctionApproximation> {
    fn clone(&self) -> Self {
        self.clone_dyn()
    }
}

pub struct RegularApproximationOptions {
    points: Vec<(f64, f64)>,
}

impl RegularApproximationOptions {
    pub fn new(points: Vec<(f64, f64)>) -> RegularApproximationOptions {
        if points.is_empty() {
            panic!("Can't build approximator without any points");
        }
        RegularApproximationOptions { points }
    }
}

/*
Used not only for polynomial approximations
*/
fn get_polynomial_coeffs(
    degree: usize,
    points: &Vec<(f64, f64)>,
) -> Result<Vec<f64>, Box<dyn Error>> {
    let rows = degree + 1;
    let mut matrix = vec![vec![0.0; rows]; rows];
    let mut vec = vec![0.0; rows];

    matrix[0][0] = points.len() as f64;
    for i in 1..(rows.pow(2)) {
        matrix[i / rows][i % rows] = points.iter().fold(0.0, |acc, el| {
            acc + el.0.powi(
                (i / rows + i % rows)
                    .try_into()
                    .expect("Degree must be small!"),
            )
        });
    }

    for i in 0..rows {
        vec[i] = points.iter().fold(0.0, |acc, el| {
            acc + el.0.powi(i.try_into().expect("Degree must be small!")) * el.1
        });
    }
    return gauss_solve(&matrix, &vec);
}

fn get_precision_parameters(src_data: &Vec<(f64, f64)>, approx: &Vec<f64>) -> (Vec<f64>, f64, f64) {
    let deviation: Vec<f64> = src_data
        .iter()
        .zip(approx.iter())
        .map(|el| el.1 - el.0 .1)
        .collect();
    let mean: f64 = approx.iter().sum::<f64>() / approx.len() as f64;
    let deviation_from_mean: f64 = src_data
        .iter()
        .fold(0.0, |acc, el| acc + (el.1 - mean).powi(2));
    let determ_coeff =
        1.0 - (deviation.iter().map(|el| el.powi(2)).sum::<f64>()) / deviation_from_mean;
    let quadratic_deviation =
        (deviation.iter().map(|el| el.powi(2)).sum::<f64>() / deviation.len() as f64).sqrt();
    return (deviation, determ_coeff, quadratic_deviation);
}

/*
========================
POLYNOMIAL APPROXIMATION
========================
*/
fn polynomial_fun(coeffs: &Vec<f64>, arg: f64) -> f64 {
    coeffs.iter().enumerate().fold(0.0, |acc, el| {
        acc + el.1 * arg.powi(el.0.try_into().expect("Degree is expected to be small."))
    })
}

#[derive(Clone)]
struct PolynomialApproximationFunction {
    degree: usize,
    coeffs: Vec<f64>,
    src_data: Vec<(f64, f64)>,
    approx: Vec<f64>,
    deviation: Vec<f64>,
    quadratic_deviation: f64,
    determ_coeff: f64,
    pirson_correlation: f64,
}
impl PolynomialApproximationFunction {
    pub fn new(
        degree: usize,
        coeffs: Vec<f64>,
        src_data: &Vec<(f64, f64)>,
    ) -> PolynomialApproximationFunction {
        let approx: Vec<f64> = src_data
            .iter()
            .map(|el| polynomial_fun(&coeffs, el.0))
            .collect();
        let (deviation, determ_coeff, quadratic_deviation) =
            get_precision_parameters(src_data, &approx);
        let x_mean: f64 = src_data.iter().map(|el| el.0).sum::<f64>() / src_data.len() as f64;
        let y_mean: f64 = src_data.iter().map(|el| el.1).sum::<f64>() / src_data.len() as f64;
        let pirson_top: f64 = src_data
            .iter()
            .map(|el| (el.0 - x_mean) * (el.1 - y_mean))
            .sum();
        let pirson_bottom: f64 = src_data
            .iter()
            .map(|el| (el.0 - x_mean).powi(2))
            .sum::<f64>()
            * src_data
                .iter()
                .map(|el| (el.1 - y_mean).powi(2))
                .sum::<f64>();
        let pirson = pirson_top / pirson_bottom.sqrt();

        PolynomialApproximationFunction {
            degree,
            coeffs,
            src_data: src_data.clone(),
            determ_coeff,
            deviation,
            approx,
            pirson_correlation: pirson,
            quadratic_deviation,
        }
    }
}

impl Display for PolynomialApproximationFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
        ======================================\n\
        Polynomial approximation of degree {}\n\
        --------------------------------------\n\
        Coefficients (a_0, a_1 ... a_n): {:?}\n\
        Determination Coefficient (R^2): {:.4}\n\
        Pirson Correlation Coeff: {:.4}\n\
        Quadratic Deviation: {:.4}\n\
        ",
            self.degree,
            self.coeffs,
            self.determ_coeff,
            self.pirson_correlation,
            self.quadratic_deviation
        )
    }
}

impl FunctionApproximation for PolynomialApproximationFunction {
    fn fun(&self, arg: f64) -> f64 {
        polynomial_fun(&self.coeffs, arg)
    }
    fn clone_dyn(&self) -> Box<dyn FunctionApproximation> {
        Box::new(self.clone())
    }

    fn legend_name(&self) -> String {
        return format!("Polynomial approx. of degree {}", self.degree);
    }
    fn sq_deviation(&self) -> f64 {
        self.quadratic_deviation
    }
    
    fn determ_coeff(&self) -> f64 {
        self.determ_coeff
    }
    
    fn deviaton_vec(&self) -> &Vec<f64> {
        &self.deviation
    }
    
    fn approx_vec(&self) -> &Vec<f64> {
        &self.approx
    }
}

pub struct PolynomialApproximation {
    degree: usize,
}

impl PolynomialApproximation {
    pub fn new(degree: usize) -> PolynomialApproximation {
        PolynomialApproximation { degree }
    }
}

impl Display for PolynomialApproximation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Polynomial of degree {} approximation", self.degree)
    }
}

impl ApproximationBuilder for PolynomialApproximation {
    type ApproximationOptions = RegularApproximationOptions;

    fn approximate(
        &self,
        opts: &Self::ApproximationOptions,
    ) -> Result<Box<dyn FunctionApproximation>, Box<dyn Error>> {
        let coeffs = get_polynomial_coeffs(self.degree, &opts.points)?;
        Ok(Box::new(PolynomialApproximationFunction::new(
            self.degree,
            coeffs,
            &opts.points,
        )))
    }
}

/*
EXPONENT APPROXIMATION
*/
fn exponent_fun(coeffs: &Vec<f64>, arg: f64) -> f64 {
    coeffs[0] * (coeffs[1] * arg).exp()
}

#[derive(Clone)]
struct ExponentApproximationFunction {
    coeffs: Vec<f64>,
    src_data: Vec<(f64, f64)>,
    approx: Vec<f64>,
    deviation: Vec<f64>,
    determ_coeff: f64,
    quadratic_deviation: f64,
}
impl ExponentApproximationFunction {
    pub fn new(coeffs: Vec<f64>, src_data: &Vec<(f64, f64)>) -> ExponentApproximationFunction {
        let approx: Vec<f64> = src_data
            .iter()
            .map(|el| exponent_fun(&coeffs, el.0))
            .collect();
        let (deviation, determ_coeff, quadratic_deviation) =
            get_precision_parameters(src_data, &approx);
        ExponentApproximationFunction {
            coeffs,
            src_data: src_data.clone(),
            determ_coeff,
            deviation,
            approx,
            quadratic_deviation,
        }
    }
}

impl Display for ExponentApproximationFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
        ======================================\n\
        Exponent approximation \n\
        --------------------------------------\n\
        Approximated func (a, b): {:.4}*e^{{{:.4}*x}}\n\
        Determination Coefficient (R^2): {:.4}\n\
        Quadratic Deviation: {:.4}\n\
        ",
            self.coeffs[0], self.coeffs[1], self.determ_coeff, self.quadratic_deviation
        )
    }
}

impl FunctionApproximation for ExponentApproximationFunction {
    fn fun(&self, arg: f64) -> f64 {
        exponent_fun(&self.coeffs, arg)
    }
    fn clone_dyn(&self) -> Box<dyn FunctionApproximation> {
        Box::new(self.clone())
    }
    fn legend_name(&self) -> String {
        "Exponent Approximation".to_string()
    }
    fn sq_deviation(&self) -> f64 {
        self.quadratic_deviation
    }
    
    fn determ_coeff(&self) -> f64 {
        self.determ_coeff
    }
    
    fn deviaton_vec(&self) -> &Vec<f64> {
        &self.deviation
    }
    
    fn approx_vec(&self) -> &Vec<f64> {
        &self.approx
    }
}

pub struct ExponentApproximation {}

impl Display for ExponentApproximation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Exponent approx")
    }
}

impl ApproximationBuilder for ExponentApproximation {
    type ApproximationOptions = RegularApproximationOptions;
    fn approximate(
        &self,
        opts: &Self::ApproximationOptions,
    ) -> Result<Box<dyn FunctionApproximation>, Box<dyn Error>> {
        let log_points = opts.points.iter().map(|el| (el.0, el.1.ln())).collect();
        let coeffs = get_polynomial_coeffs(1, &log_points)?;
        Ok(Box::new(ExponentApproximationFunction::new(
            vec![coeffs[0].exp(), coeffs[1]],
            &opts.points,
        )))
    }
}

/*
POWER APPROXIMATION
*/
fn power_fun(coeffs: &Vec<f64>, arg: f64) -> f64 {
    coeffs[0] * arg.powf(coeffs[1])
}

#[derive(Clone)]
struct PowerApproximationFunction {
    coeffs: Vec<f64>,
    src_data: Vec<(f64, f64)>,
    approx: Vec<f64>,
    deviation: Vec<f64>,
    determ_coeff: f64,
    quadratic_deviation: f64,
}
impl PowerApproximationFunction {
    pub fn new(coeffs: Vec<f64>, src_data: &Vec<(f64, f64)>) -> PowerApproximationFunction {
        let approx: Vec<f64> = src_data.iter().map(|el| power_fun(&coeffs, el.0)).collect();
        let (deviation, determ_coeff, quadratic_deviation) =
            get_precision_parameters(src_data, &approx);
        PowerApproximationFunction {
            coeffs,
            src_data: src_data.clone(),
            determ_coeff,
            deviation,
            approx,
            quadratic_deviation,
        }
    }
}

impl Display for PowerApproximationFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
        ======================================\n\
        Power approximation \n\
        --------------------------------------\n\
        Approximated func (a, b): {:.4}*x^{{{:.4}}}\n\
        Determination Coefficient (R^2): {:.4}\n\
        Quadratic Deviation: {:.4}\n\
        ",
            self.coeffs[0], self.coeffs[1], self.determ_coeff, self.quadratic_deviation
        )
    }
}

impl FunctionApproximation for PowerApproximationFunction {
    fn fun(&self, arg: f64) -> f64 {
        power_fun(&self.coeffs, arg)
    }
    fn clone_dyn(&self) -> Box<dyn FunctionApproximation> {
        Box::new(self.clone())
    }
    fn legend_name(&self) -> String {
        "Power Approximation".to_string()
    }
    fn sq_deviation(&self) -> f64 {
        self.quadratic_deviation
    }
    
    fn determ_coeff(&self) -> f64 {
        self.determ_coeff
    }
    
    fn deviaton_vec(&self) -> &Vec<f64> {
        &self.deviation
    }
    
    fn approx_vec(&self) -> &Vec<f64> {
        &self.approx
    }
}

pub struct PowerApproximation {}

impl Display for PowerApproximation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Power approx")
    }
}

impl ApproximationBuilder for PowerApproximation {
    type ApproximationOptions = RegularApproximationOptions;
    fn approximate(
        &self,
        opts: &Self::ApproximationOptions,
    ) -> Result<Box<dyn FunctionApproximation>, Box<dyn Error>> {
        let log_points = opts
            .points
            .iter()
            .map(|el| (el.0.ln(), el.1.ln()))
            .collect();
        let coeffs = get_polynomial_coeffs(1, &log_points)?;
        Ok(Box::new(PowerApproximationFunction::new(
            vec![coeffs[0].exp(), coeffs[1]],
            &opts.points,
        )))
    }
}

/*
LOGARITHMIC APPROXIMATION
*/
fn logarithm_fun(coeffs: &Vec<f64>, arg: f64) -> f64 {
    coeffs[0] + coeffs[1] * arg.ln()
}

#[derive(Clone)]
struct LogarithmApproximationFunction {
    coeffs: Vec<f64>,
    src_data: Vec<(f64, f64)>,
    approx: Vec<f64>,
    deviation: Vec<f64>,
    determ_coeff: f64,
    quadratic_deviation: f64,
}
impl LogarithmApproximationFunction {
    pub fn new(coeffs: Vec<f64>, src_data: &Vec<(f64, f64)>) -> LogarithmApproximationFunction {
        let approx: Vec<f64> = src_data
            .iter()
            .map(|el| logarithm_fun(&coeffs, el.0))
            .collect();
        let (deviation, determ_coeff, quadratic_deviation) =
            get_precision_parameters(src_data, &approx);
        LogarithmApproximationFunction {
            coeffs,
            src_data: src_data.clone(),
            determ_coeff,
            deviation,
            approx,
            quadratic_deviation,
        }
    }
}

impl Display for LogarithmApproximationFunction {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "\
        ======================================\n\
        Logarithmic approximation \n\
        --------------------------------------\n\
        Approximated func (a, b): {:.4}+{:.4}*ln(x)\n\
        Determination Coefficient (R^2): {:.4}\n\
        Quadratic Deviation: {:.4}\n\
        ",
            self.coeffs[0], self.coeffs[1], self.determ_coeff, self.quadratic_deviation
        )
    }
}

impl FunctionApproximation for LogarithmApproximationFunction {
    fn fun(&self, arg: f64) -> f64 {
        logarithm_fun(&self.coeffs, arg)
    }
    fn clone_dyn(&self) -> Box<dyn FunctionApproximation> {
        Box::new(self.clone())
    }
    fn legend_name(&self) -> String {
        "Logarithmic Approximation".to_string()
    }
    fn sq_deviation(&self) -> f64 {
        self.quadratic_deviation
    }
    
    fn determ_coeff(&self) -> f64 {
        self.determ_coeff
    }
    
    fn deviaton_vec(&self) -> &Vec<f64> {
        &self.deviation
    }
    
    fn approx_vec(&self) -> &Vec<f64> {
        &self.approx
    }
}

pub struct LogarithmApproximation {}

impl Display for LogarithmApproximation {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Logarithm approx")
    }
}

impl ApproximationBuilder for LogarithmApproximation {
    type ApproximationOptions = RegularApproximationOptions;
    fn approximate(
        &self,
        opts: &Self::ApproximationOptions,
    ) -> Result<Box<dyn FunctionApproximation>, Box<dyn Error>> {
        let log_points = opts.points.iter().map(|el| (el.0.ln(), el.1)).collect();
        let coeffs = get_polynomial_coeffs(1, &log_points)?;
        Ok(Box::new(LogarithmApproximationFunction::new(
            vec![coeffs[0], coeffs[1]],
            &opts.points,
        )))
    }
}
