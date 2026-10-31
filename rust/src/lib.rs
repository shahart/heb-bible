use anyhow::Result;
use flate2::read::GzDecoder;
use std::fs::File;
use std::io::{BufRead, BufReader};

pub struct HebBible {
    repo: Vec<String>,
}

impl HebBible {
    pub fn new(gz_path: &str) -> Result<Self> {
        let file = File::open(gz_path)?;
        let decoder = GzDecoder::new(file);
        let reader = BufReader::new(decoder);

        let mut repo = Vec::new();

        for line in reader.lines() {
            let l = line?;
            let parts: Vec<&str> = l.split(',').collect();
            if parts.len() > 1 {
                repo.push(parts[1].to_string());
            }
        }

        println!("{} psukim", repo.len());

        Ok(HebBible { repo })
    }

    pub fn total_psukim(&self) -> usize {
        self.repo.len()
    }

    pub fn psukim_by_name(&self, name: &str, contains_name: bool) -> usize {
        println!("/get {}", name);
        if name.is_empty() {
            return 0;
        }
        let first = name.chars().next();
        let last = name.chars().last();

        let mut count = 0usize;
        for psk in &self.repo {
            let f = psk.chars().next();
            let l = psk.chars().last();
            if (f == first && l == last) || (contains_name && psk.contains(name)) {
                count += 1;
            }
        }
        println!("{}", count);
        count
    }
}

#[cfg(test)]
mod tests {
    use super::HebBible;

    fn bible() -> HebBible {
        HebBible {
            repo: vec![
                "אבג".to_string(),
                "אxyzג".to_string(),
                "אבxד".to_string(),
                "hello world".to_string(),
                "say hello".to_string(),
                "xhelloy".to_string(),
            ],
        }
    }

    #[test]
    fn contains_name_counts_substrings_and_boundary_matches() {
        let bible = bible();
        assert_eq!(bible.psukim_by_name("hell", true), 3);
        assert_eq!(bible.psukim_by_name("אxyzד", true), 1);
    }

    #[test]
    fn contains_name_false_keeps_boundary_matching() {
        let bible = bible();
        assert_eq!(bible.psukim_by_name("אanythingג", false), 2);
        assert_eq!(bible.psukim_by_name("hell", false), 0);
    }
}
