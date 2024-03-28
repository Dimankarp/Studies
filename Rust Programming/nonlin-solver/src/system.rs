use crate::equation::Equation;

#[derive(Clone)]
pub struct System {
     size: usize,
     equations: Vec<Equation>,
}

impl System {
    pub fn new(size: usize, equations: Vec<Equation>) -> Self {
        if equations.len() != size {
            panic!("Number of equations doesn't equal to size of the system.");
        }
        if equations.iter().any(|eq| eq.fun().vars_count() != size) {
            panic!("Some equations have number variables different from the size of the system.");
        }
        System { size, equations }
    }

    pub fn size(&self) -> usize {
        self.size
    }

    pub fn equations(&self) -> &Vec<Equation> {
        &self.equations
    }
}

impl std::fmt::Display for System {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        for row in 0..self.size {
            if row == 0 {
                write!(f, "/")?;
            } else if row == self.size - 1 {
                write!(f, "\\")?;
            } else {
                write!(f, "|")?;
            }
            write!(f, "{}", self.equations[row])?;
            write!(f, "\n")?;
        }
        Ok(())
    }
}
