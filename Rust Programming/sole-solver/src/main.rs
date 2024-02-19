use sole_solver::Config;
use std::{env, process};

fn main() {
    let config: Config = Config::build(env::args()).unwrap_or_else(|err| {
        eprintln!("Failed to configure start: {err}");
        process::exit(1);
    });

    match sole_solver::run(config) {
        Ok(solution) => {
            println!("{}", solution);
            process::exit(0);
        }
        Err(err) => {
            eprintln!("An error occured while running the program: {err}!");
            process::exit(1);
        }
    }
}
