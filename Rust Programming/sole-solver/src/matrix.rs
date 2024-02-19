use std::ops::{Add, Index, IndexMut, Mul};

pub struct Matrix<T> {
    rows: usize,
    cols: usize,
    table: Vec<Vec<T>>,
}

impl<T> Matrix<T> {
    pub fn new(table: Vec<Vec<T>>) -> Matrix<T> {
        let first_col_len = if table.is_empty() { 0 } else { table[0].len() };
        for col in &table {
            if col.len() != first_col_len {
                panic!("Jugged table was provided for matrix creation!");
            }
        }

        Matrix {
            rows: table.len(),
            cols: if table.is_empty() { 0 } else { table[0].len() },
            table,
        }
    }

    pub fn new_square(table: Vec<Vec<T>>) -> Matrix<T> {
        let first_col_len = if table.is_empty() { 0 } else { table[0].len() };
        if first_col_len != table.len() {
            panic!("Rectangular table was provided for square matrix creation!");
        }
        for col in &table {
            if col.len() != first_col_len {
                panic!("Jugged table was provided for matrix creation!");
            }
        }

        Matrix {
            rows: table.len(),
            cols: if table.is_empty() { 0 } else { table[0].len() },
            table,
        }
    }

    pub fn rows(&self) -> usize {
        self.rows
    }
    pub fn cols(&self) -> usize {
        self.cols
    }
    pub fn table(&self) -> &Vec<Vec<T>> {
        &self.table
    }

    pub fn swap_rows(&mut self, a: usize, b: usize) {
        self.table.swap(a, b);
    }
}

/*
   Additional functionality based on traits implemented on T
*/

impl<T: Add<Output = T> + Copy + Mul<Output = T>> Matrix<T> {
    pub fn add_to_row(&mut self, from_row: usize, to_row: usize, multiplier: T) {
        if !(0..self.rows).contains(&from_row) || !(0..self.rows).contains(&to_row) {
            panic!("Invalid row indexes were provided for add_to_row.")
        }

        for i in 0..self.cols {
            self[to_row][i] = self[to_row][i] + self[from_row][i] * multiplier;
        }
    }
}

/*
   Basic operator overloading with Traits
*/
impl<T: Clone> Clone for Matrix<T> {
    fn clone(&self) -> Self {
        Self {
            rows: self.rows,
            cols: self.cols,
            table: self.table.clone(),
        }
    }
}
impl<T> Index<usize> for Matrix<T> {
    type Output = Vec<T>;

    fn index(&self, index: usize) -> &Self::Output {
        &self.table[index]
    }
}

impl<T> IndexMut<usize> for Matrix<T> {
    fn index_mut(&mut self, index: usize) -> &mut Self::Output {
        &mut self.table[index]
    }
}

impl Matrix<f64> {
    pub fn determinant(&self) -> f64 {
        if self.cols != self.rows {
            panic!("Can't find determinant for a non-square matrix!");
        }
        if self.cols == 0 {
            panic!("Can't find determinant for an empty matrix!");
        }

        let mut mtrx = self.clone();
        let mut determinant = 1.0;

        for row in 0..mtrx.rows {
            let mut curr_max = mtrx[row][row];
            let mut curr_max_row = row;
            for i in row + 1..mtrx.rows {
                if mtrx[i][row].abs() > curr_max.abs() {
                    curr_max_row = i;
                    curr_max = mtrx[i][row];
                }
            }

            if row != curr_max_row {
                mtrx.table.swap(row, curr_max_row);
                determinant *= -1.0;
            }

            //Checking for a true 0 column
            if curr_max == 0.0 {
                return 0.0;
            }
            determinant *= curr_max;

            //Zeroing column
            for i in row + 1..mtrx.rows {
                mtrx.add_to_row(row, i, -mtrx[i][row] / mtrx[row][row]);
            }
        }
        return determinant;
    }
}

#[cfg(test)]
mod tests {

    use super::*;
    #[test]
    fn zero_determinant_check() {
        let mtrx = Matrix::new_square(vec![
            vec![1.0, 2.0, 3.0],
            vec![4.0, 5.0, 6.0],
            vec![7.0, 8.0, 9.0],
        ]);
        println!("Determinant is:{}", mtrx.determinant());
        assert!((mtrx.determinant() - 0.0).abs() < 0.000001)
    }
    #[test]
    fn regular_determinant_check_1() {
        let mtrx = Matrix::new_square(vec![
            vec![1.0, 3.0, 2.0],
            vec![-3.0, -1.0, -3.0],
            vec![2.0, 3.0, 1.0],
        ]);
        println!("Determinant is:{}", mtrx.determinant());
        assert!((mtrx.determinant() - (-15.0)).abs() < 0.00001)
    }

    #[test]
    fn regular_determinant_check_2() {
        let mtrx = Matrix::new_square(vec![
            vec![1.0, -2.0, 3.0],
            vec![2.0, 0.0, 3.0],
            vec![1.0, 5.0, 4.0],
        ]);
        println!("Determinant is:{}", mtrx.determinant());
        assert!((mtrx.determinant() - (25.0)).abs() < 0.00001)
    }

    #[test]
    fn regular_determinant_check_3() {
        let mtrx = Matrix::new_square(vec![
            vec![3.0, 1.0, 1.0],
            vec![4.0, -2.0, 5.0],
            vec![2.0, 8.0, 7.0],
        ]);
        println!("Determinant is:{}", mtrx.determinant());
        assert!((mtrx.determinant() - (-144.0)).abs() < 0.00001)
    }
    #[test]
    fn _4x4_determinant_check() {
        let mtrx = Matrix::new_square(vec![
            vec![2.0, 0.5, 1.0, 5.0],
            vec![2.0, 0.3, 7.0, 100.0],
            vec![65.0, 89.0, 12.0, 31.0],
            vec![6.0, -200.0, 2.0, 1.0],
        ]);
        println!("Determinant is:{}", mtrx.determinant());
        assert!((mtrx.determinant() - (-468538.2)).abs() < 10.0)
    }
}
