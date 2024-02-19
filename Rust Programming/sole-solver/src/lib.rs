use matrix::Matrix;
use regex::Regex;
use sole::SOLE;
use std::{
    error::Error,
    fs::File,
    io::{self, Read},
    num::ParseFloatError,
};

mod matrix;
mod sole;
mod solver;

pub struct Config {
    file: Option<File>,
}

impl Config {
    pub fn build(mut args: impl Iterator<Item = String>) -> Result<Self, String> {
        args.next(); //Skipping program name
        let mut file = None;

        while let Some(arg) = args.next() {
            match arg.as_str() {
                "-f" => {
                    let path = match args.next() {
                        Some(arg) => arg,
                        None => {
                            return Err(
                                "Filepath wasn't immediately provided after -f key".to_string()
                            )
                        }
                    };

                    file = match File::open(&path) {
                        Ok(arg) => Some(arg),
                        Err(err) => {
                            return Err(String::from(format!(
                                "Failed to open file {path} because of: {err}"
                            )));
                        }
                    };
                }
                _ => {
                    return Err(String::from(format!("Invalid argument provided: {}", arg)));
                }
            }
        }
        return Ok(Config { file });
    }
}

pub struct SOLESolution {
    pub x_vec: Vec<f64>,
    pub diff_vec: Vec<f64>,
    pub iterations: usize,
}

impl std::fmt::Display for SOLESolution {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "SOLUTION:\n\n\
            (x_i) -> {:?}\n\n\
            (|x_i^(k) - x_i^(k)|) -> {:?}\n\
            where x_i^(k) -> x_i from k-th iteration\n\n\
            Was found in {} iterations.",
            self.x_vec, self.diff_vec, self.iterations
        )
    }
}

struct ParseResult {
    pub table: Vec<Vec<f64>>,
    pub precision: f64,
}

pub fn run(config: Config) -> Result<SOLESolution, Box<dyn Error>> {
    let mut parse_result: ParseResult;
    match config.file {
        Some(file) => {
            parse_result = input_from_file(&file)?;
        }
        None => {
            parse_result = input_from_stdin()?;
        }
    }
    let sole: SOLE;
    let precision: f64;
    precision = parse_result.precision;
    let coeff_vec = parse_result
        .table
        .iter()
        .map(|row| {
            row.last()
                .copied()
                .expect("Since parse_result table can't be empty at this point - it works")
        })
        .collect();
    parse_result.table.iter_mut().for_each(|row| {
        row.pop();
    });
    sole = SOLE::build(Matrix::new_square(parse_result.table), coeff_vec);

    println!("Trying to solve:\n{}", sole);
    println!("With precision e={precision}");
    let solution = solver::solve(&sole, precision)?;
    return Ok(solution);
}

fn input_from_file(mut file: &File) -> Result<ParseResult, String> {
    let mut file_input = String::new();
    if let Err(err) = file.read_to_string(&mut file_input) {
        return Err(format!("Failed to read provided file because of: {err}"));
    }

    let precision_re =
        Regex::new(r"(?m)^e=(\d+\.?\d+)").expect("The regex must compile. It's tested");
    let matrix_re = Regex::new(r"(?m)---(?:\n|\r\n)((?:\s*[-+]?\d+\.?\d*\s*\n?)+)(?:\n|\r\n)---")
        .expect("The regex must compile. It's tested");

    let precision: f64;
    let table: Vec<Vec<f64>>;

    if let Some((_, [epsilon])) = precision_re
        .captures_iter(&file_input)
        .map(|c| c.extract())
        .next()
    {
        //Reading first match;
        precision = match epsilon.parse() {
            Ok(val) => val,
            Err(_) => {
                return Err(format!(
                    "Failed to parse precision: {epsilon}. Please use valid float form."
                ));
            }
        };
    } else {
        return Err(format!(
            "Failed to find precision line in the file. Expected to see: \"e=XXX.XXX\""
        ));
    }

    if let Some((_, [matrix_str])) = matrix_re
        .captures_iter(&file_input)
        .map(|c| c.extract())
        .next()
    {
        let lines: Vec<Vec<&str>> = matrix_str
            .split('\n')
            .map(|line| line.split_ascii_whitespace().collect())
            .collect();

        //Square matrix only (plus the result column)
        let rows = lines.len();
        let cols = lines[0].len();
        for i in 1..lines.len() {
            if lines[i].len() != cols {
                return Err(
                    format!("Jagged matrices can't be solved (columns in first line - {cols} != in {i} line)! At least fill it with zeroes or smth"),
                );
            }
        }
        if rows != cols - 1 {
            return Err(format!("Only square matrix solving is implemented! Please, provide matrix in a form of nx(n+1) (+1 for coeff column)"));
        }

        table = match lines
            .iter()
            .map(|line| {
                line.iter()
                    .map(|num| (*num).parse::<f64>())
                    .collect::<Result<Vec<f64>, ParseFloatError>>()
            })
            .collect::<Result<Vec<Vec<f64>>, ParseFloatError>>()
        {
            Ok(val) => val,
            Err(err) => {
                return Err(format!("Failed to parse matrix element: {err}"));
            }
        }
    } else {
        return Err(format!("Failed to find matrix lines in the file. Expected to see: \n---\nX.XXX X.XXX X.XXX...\n...\nX.XXX X.XXX X.XXX\n---\n"));
    }
    Ok(ParseResult { table, precision })
}

fn input_from_stdin() -> Result<ParseResult, Box<dyn Error>> {
    println!("Please, enter the size of the SOLE matrix (n) as integer:");
    let n: usize = loop {
        let mut input = String::new();
        io::stdin().read_line(&mut input)?;
        break match input.trim().parse() {
            Ok(num) => num,
            Err(_) => {
                println!("Failed to parse provided value as integer. Please, try again:");
                continue;
            }
        };
    };
    let mut table: Vec<Vec<f64>> = Vec::new();
    println!(
        "Please, enter {} floating-point numbers in a line separated by spaces\
     ({} first values - coefficients of x_i and additional value - free coefficient):",
        n + 1,
        n
    );
    for i in 0..n {
        println!("Expecting {} values for row {}", n + 1, i + 1);
        loop {
            let row: Vec<f64> = loop {
                let mut input = String::new();
                io::stdin().read_line(&mut input)?;
                break match input
                    .trim()
                    .split_ascii_whitespace()
                    .map(|num| num.parse::<f64>())
                    .collect()
                {
                    Ok(num) => num,
                    Err(_) => {
                        println!("Failed to parse provided value as double. Please, try again:");
                        continue;
                    }
                };
            };
            if row.len() == n + 1 {
                table.push(row);
                break;
            } else {
                println!("Expected {} values, but {} were provided", n + 1, row.len());
            }
        }
    }
    println!("Please, enter required precision (e) as float:");
    let precision: f64 = loop {
        let mut input = String::new();
        io::stdin().read_line(&mut input)?;
        break match input.trim().parse() {
            Ok(num) => num,
            Err(_) => {
                println!("Failed to parse provided value as float. Please, try again:");
                continue;
            }
        };
    };

    Ok(ParseResult { table, precision })
}

#[cfg(test)]
mod tests {

    use std::{
        fs::remove_file,
        io::{Seek, Write},
        vec,
    };

    use super::*;
    #[test]
    fn parse_file_valid() {
        {
            let mut f = File::create("parse_file_valid").expect("It's a test");
            f.write_all(
                b"---
1 1 1 1 1
1 2 3 4.0 1
5.1 5 6 7 1
10 199 12 -10 1\n---\ne=10.11",
            )
            .expect("It's a test - please make it work");
            f.flush().expect("It's a test - please make it work");
            f.rewind().expect("It's a test - please make it work");
        }
        let f = File::open("parse_file_valid").expect("It exist!");
        let res = input_from_file(&f).expect("It's a valid test");
        remove_file("parse_file_valid").expect("Do smth if it doesnt work! It should!");
        assert_eq!(res.precision, 10.11);
        assert_eq!(
            res.table,
            vec![
                vec![1.0, 1.0, 1.0, 1.0, 1.0],
                vec![1.0, 2.0, 3.0, 4.0, 1.0],
                vec![5.1, 5.0, 6.0, 7.0, 1.0],
                vec![10.0, 199.0, 12.0, -10.0, 1.0],
            ]
        );
    }
}
