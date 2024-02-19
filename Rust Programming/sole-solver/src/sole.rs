use std::fmt::Display;

use crate::matrix::Matrix;

#[derive(Clone)]
pub struct SOLE {
    pub matrix: Matrix<f64>,
    pub coeff_vec: Vec<f64>,
}

impl SOLE {
    pub fn build(matrix: Matrix<f64>, coeff_vec: Vec<f64>) -> SOLE {
        if matrix.rows() != matrix.cols() {
            panic!("This implementation doesn't support non-square matrices!")
        }
        if matrix.rows() == 0 {
            panic!("Can't create SOLE with empty matrix.")
        }
        if coeff_vec.len() != matrix.rows() {
            panic!("The number of rows in the provided matrix is different from the length of the free coefficients vector!")
        }
        SOLE { matrix, coeff_vec }
    }

    pub fn swap_rows(&mut self, a: usize, b: usize) {
        self.matrix.swap_rows(a, b);
        self.coeff_vec.swap(a, b);
    }
}

impl Display for SOLE {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        for row in 0..self.coeff_vec.len() {
            if row == 0 {
                write!(f, "/")?;
            } else if row == self.coeff_vec.len() - 1 {
                write!(f, "\\")?;
            } else {
                write!(f, "|")?;
            }
            for col in 0..self.matrix[0].len() {
                write!(f, " {number:>10}", number = self.matrix[row][col])?;
            }

            if row == 0 {
                write!(f, "| {number:>10}\\", number = self.coeff_vec[row])?;
            } else if row == self.coeff_vec.len() - 1 {
                write!(f, "| {number:>10}/", number = self.coeff_vec[row])?;
            } else {
                write!(f, "| {number:>10}|", number = self.coeff_vec[row])?;
            }
            write!(f, "\n")?;
        }
        Ok(())
    }
}

/*
   Checking all the permutations with Heap's Algorithm
*/
pub fn get_diagonally_dominated(sole: &SOLE) -> Result<SOLE, &'static str> {
    let mut temp = sole.clone();
    let mut c: Vec<usize> = vec![0; sole.matrix.rows()];

    if is_diagonally_dominated(&temp) {
        return Ok(temp);
    }

    let mut i: usize = 1;
    while i < sole.matrix.rows() {
        if c[i] < i {
            if i % 2 == 0 {
                temp.swap_rows(0, i);
            } else {
                temp.swap_rows(c[i], i);
            }
            if is_diagonally_dominated(&temp) {
                return Ok(temp);
            }
            c[i] += 1;
            i = 1;
        } else {
            c[i] = 0;
            i += 1;
        }
    }
    Err("No row permutation ensures diagonal domination for provided SOLE")
}

fn is_diagonally_dominated(sole: &SOLE) -> bool {
    let mut is_strongly_dominated = false;
    for row in 0..sole.matrix.rows() {
        let row_sum: f64 = sole.matrix[row].iter().map(|a| a.abs()).sum();
        if sole.matrix[row][row].abs() > row_sum - sole.matrix[row][row].abs() {
            is_strongly_dominated = true;
        } else if sole.matrix[row][row].abs() == row_sum - sole.matrix[row][row].abs() {
            continue;
        } else {
            return false;
        }
    }
    return is_strongly_dominated;
}

#[cfg(test)]
mod tests {

    use super::*;
    #[test]
    fn is_diag_domination() {
        let mtrx = Matrix::new_square(vec![
            vec![100.0, 2.0, 3.0],
            vec![4.0, 54.0, 6.0],
            vec![7.0, 8.0, 97.0],
        ]);
        let coeff = vec![1.0, 2.0, 3.0];

        let sole = SOLE::build(mtrx, coeff);
        assert!(is_diagonally_dominated(&sole));
    }

    #[test]
    fn possible_diag_domination() {
        let mtrx = Matrix::new_square(vec![
            vec![7.0, 8.0, 97.0],
            vec![4.0, 54.0, 6.0],
            vec![100.0, 2.0, 3.0],
        ]);
        let coeff = vec![3.0, 2.0, 1.0];

        let sole = get_diagonally_dominated(&SOLE::build(mtrx, coeff)).unwrap();
        assert_eq!(
            sole.matrix.table().clone(),
            vec![
                vec![100.0, 2.0, 3.0],
                vec![4.0, 54.0, 6.0],
                vec![7.0, 8.0, 97.0]
            ]
        );
        assert_eq!(sole.coeff_vec, vec![1.0, 2.0, 3.0])
    }

    #[test]
    fn possible_diag_domination_mixed_cols() {
        let mtrx = Matrix::new_square(vec![
            vec![3.0, 2.0, 100.0],
            vec![6.0, 54.0, 4.0],
            vec![97.0, 0.0, 97.0],
        ]);
        let coeff = vec![1.0, 2.0, 3.0];

        let sole = get_diagonally_dominated(&SOLE::build(mtrx, coeff)).unwrap();
        assert_eq!(
            sole.matrix.table().clone(),
            vec![
                vec![97.0, 0.0, 97.0],
                vec![6.0, 54.0, 4.0],
                vec![3.0, 2.0, 100.0],
            ]
        );
        assert_eq!(sole.coeff_vec, vec![3.0, 2.0, 1.0])
    }
}
