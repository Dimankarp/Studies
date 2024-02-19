use crate::{
    sole::{self, SOLE},
    SOLESolution,
};

pub fn solve(sole: &SOLE, precision: f64) -> Result<SOLESolution, String> {
    if sole.matrix.determinant().abs() < 0.00001 {
        return Err(format!(
            "Provided SOLE doesn't have a single solution (determinant is zero)"
        ));
    }
    let sole = sole::get_diagonally_dominated(&sole)?;
    let mut prev_vec = sole.coeff_vec.clone();
    let mut new_vec = vec![0.0; prev_vec.len()];
    let mut iteration_count = 0;

    loop {
        iterate_filling_vec(&mut new_vec, &mut prev_vec, &sole);
        iteration_count += 1;

        if get_max_diff(&new_vec, &prev_vec) < precision {
            return Ok(SOLESolution {
                iterations: iteration_count,
                diff_vec: get_diff_vec(&new_vec, &prev_vec),
                x_vec: new_vec,
            });
        }
        if iteration_count > 1000 {
            return Err(format!(
                "Failed to meet precision in {iteration_count} iterations!"
            ));
        }
        (prev_vec, new_vec) = (new_vec, prev_vec);
    }
}

fn iterate_filling_vec(new: &mut Vec<f64>, prev: &Vec<f64>, sole: &SOLE) {
    if new.len() != prev.len() {
        panic!("Expect equal length new and prev vectors");
    }
    if new.len() != sole.coeff_vec.len() {
        panic!("Expect vectors to have a length of coeff vector of SOLE");
    }

    for row in 0..new.len() {
        new[row] = sole.coeff_vec[row];
        for col in 0..row {
            new[row] -= prev[col] * sole.matrix[row][col]
        }

        for col in row + 1..sole.matrix.cols() {
            new[row] -= prev[col] * sole.matrix[row][col]
        }

        new[row] /= sole.matrix[row][row];
    }
}

fn get_max_diff(a: &Vec<f64>, b: &Vec<f64>) -> f64 {
    if a.len() != b.len() {
        panic!(
            "Expected vectors of the same size, but a is {} and b is {}",
            a.len(),
            b.len()
        );
    }
    if a.len() == 0 {
        panic!("Expected non-empty vectors!");
    }

    let mut curr_max_diff = (b[0] - a[0]).abs();
    for i in 1..a.len() {
        curr_max_diff = f64::max(curr_max_diff, (b[i] - a[i]).abs());
    }
    return curr_max_diff;
}

fn get_diff_vec(a: &Vec<f64>, b: &Vec<f64>) -> Vec<f64> {
    if a.len() != b.len() {
        panic!(
            "Expected vectors of the same size, but a is {} and b is {}",
            a.len(),
            b.len()
        );
    }
    if a.len() == 0 {
        panic!("Expected non-empty vectors!");
    }
    let mut diff = vec![0.0; a.len()];
    for i in 0..a.len() {
        diff[i] = (a[i] - b[i]).abs();
    }
    return diff;
}

#[cfg(test)]
mod tests {

    use crate::matrix::Matrix;

    use super::*;

    #[test]
    fn solve_basic() {
        let sole = SOLE {
            coeff_vec: vec![1.0, 1.0, 2.0],
            matrix: Matrix::new_square(vec![
                vec![2.0, 0.0, 1.0],
                vec![-1.0, 3.0, 0.0],
                vec![0.0, -2.0, -3.0],
            ]),
        };

        let solution = solve(&sole, 0.00001).expect("It's a test");
        println!("Iterations: {}", solution.iterations);
        assert!(get_max_diff(&solution.x_vec, &vec![1.0625, 0.6875, -1.125]) < 0.01);
    }

    #[test]
    fn solve_hard() {
        let sole = SOLE {
            coeff_vec: vec![-5.0, 6.0, 1000.0, 6.0, 1.0],
            matrix: Matrix::new_square(vec![
                vec![70.0, 0.825, -0.3, 50.0, 0.5],
                vec![0.125, 890.0, 2.0, 500.0, 200.0],
                vec![0.9, 0.6, 81.0, 70.0, 0.1],
                vec![46.0, 0.2, 51.0, 200.0, 70.0],
                vec![0.1, 0.2, -0.3, -0.67, 100.0],
            ]),
        };

        let solution = solve(&sole, 0.0001).expect("It's a test");
        println!("Iterations: {}", solution.iterations);
        println!("Solution: {:?}", &solution.x_vec);
        assert!(
            get_max_diff(
                &solution.x_vec,
                &vec![
                    3.5697117869239218751546484401298,
                    2.7987763886322087318486798382292,
                    16.644344358458735722451639238467,
                    -5.0440799502836125315959692972348,
                    0.01697043284428766386680991530734
                ]
            ) < 0.01
        );
    }

    #[test]
    #[should_panic(expected = "zero")]
    fn solve_determ_zero() {
        let sole = SOLE {
            coeff_vec: vec![1.0, 3.0],
            matrix: Matrix::new_square(vec![vec![1.0, 2.0], vec![3.0, 6.0]]),
        };

        let _ = solve(&sole, 0.0001).expect("This should panic");
    }
}
