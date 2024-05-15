use std::error::Error;

pub fn gauss_solve(matrix: &Vec<Vec<f64>>, vec: &Vec<f64>) -> Result<Vec<f64>, Box<dyn Error>> {
    if matrix.is_empty() {
        panic!("Can't solve an empty matrix!");
    }
    let rows = matrix.len();
    let cols = matrix[0].len();
    if rows != cols {
        panic!("Can't solve non-square matrix!");
    }
    for i in matrix {
        if i.len() != cols {
            panic!("Provided matrix is corrupted!");
        }
    }
    if rows != vec.len() {
        panic!("Vector of answers is not the compliant with the matrix!");
    }

    let mut mtrx = matrix.clone();
    let mut vec = vec.clone();
    for row in 0..rows {
        let mut curr_max = mtrx[row][row];
        let mut curr_max_row = row;
        for i in row + 1..rows {
            if mtrx[i][row].abs() > curr_max.abs() {
                curr_max_row = i;
                curr_max = mtrx[i][row];
            }
        }

        if row != curr_max_row {
            mtrx.swap(row, curr_max_row);
            vec.swap(row, curr_max_row);
        }

        //Checking for a true 0 column
        if curr_max == 0.0 {
            return Err(format!("Couldn't continue solving with gauss talbe: {:?} on row: {} because curr max is exaclty zero", matrix, curr_max).into());
        }
        //Zeroing column
        for i in row + 1..rows {
            let mult = -mtrx[i][row] / mtrx[row][row];
            for j in row..cols {
                mtrx[i][j] += mult * mtrx[row][j];
            }
            vec[i] += vec[row] * mult;
        }
    }

    //Back cycle

    for i in (0..rows).rev() {
        for j in (i + 1..rows).rev() {
            vec[i] -= mtrx[i][j] * vec[j];
        }
        vec[i] /= mtrx[i][i];
    }

    return Ok(vec);
}