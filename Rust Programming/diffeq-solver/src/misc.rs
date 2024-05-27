use std::{error::Error, fmt::Display};

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

pub fn get_table_str<T, A>(
    header: &Vec<T>,
    row: Vec<&Vec<A>>,
    padd: usize,
    precision: usize,
) -> String
where
    T: Display,
    A: Display,
{
    let header_str = header
        .iter()
        .map(|el| format!("{:>padd$}", el))
        .collect::<Vec<String>>()
        .join(" | ");
    let rows_str = row
        .iter()
        .map(|row| {
            row.iter()
                .map(|el| format!("{:>padd$.precision$}", el))
                .collect::<Vec<String>>()
                .join(" | ")
        })
        .collect::<Vec<String>>()
        .join("\n");
    return format!("{header_str}\n{rows_str}");
}

pub fn get_table_str_shortened<T, A>(
    header: &Vec<T>,
    row: Vec<&Vec<A>>,
    padd: usize,
    precision: usize,
    count: usize
) -> String
where
    T: Display,
    A: Display,
{
    let start_length = row.len().min(count/2);
    let end_length = (row.len()-start_length).min(count - count/2);
    let rows_slice = row.iter().take(count/2).chain(row.iter().rev().take(end_length).rev()).map(|el| *el).collect();
    get_table_str(header, rows_slice, 8, 3)
    
}

pub fn tridiagonal_solve(
    mut a: Vec<f64>,
    mut b: Vec<f64>,
    mut c: Vec<f64>,
    mut d: Vec<f64>,
) -> Vec<f64> {
    let size = b.len();
    if a.len() != size-1 || c.len() != size-1 || d.len() != size {
        panic!("Provided vectors have conflicting sizes: a_size={}, b_size={}, c_size={}, d_size={}", a.len(), b.len(), c.len(), d.len());
    }

    for i in 1..size {
        let temp = a[i-1] / b[i - 1];
        b[i] = b[i] - temp * c[i - 1];
        d[i] = d[i] - temp * d[i - 1];
    }
    let mut answer = vec![0.0; size];
    answer[size - 1] = d[size - 1] / b[size - 1];

    for i in (0..=size - 2).rev() {
        answer[i] = (d[i] - c[i] * answer[i + 1]) / b[i];
    }
    return answer;
}
