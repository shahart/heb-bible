use axum::{extract::Path, routing::get, Json, Router};
use serde::Serialize;
use std::sync::Arc;
use tokio::net::TcpListener;

use hebbible_rs::HebBible;

#[derive(Serialize)]
struct CountResponse {
    count: usize,
}

#[tokio::main]
async fn main() {
    // default path matches python version: '../bible.txt.gz'
    let heb = match HebBible::new("../bible.txt.gz") {
        Ok(h) => Arc::new(h),
        Err(e) => {
            eprintln!("Error loading bible: {}", e);
            std::process::exit(1);
        }
    };

    let app = Router::new()
        .route("/psukim", get({
            let heb = heb.clone();
            move || {
                let heb = heb.clone();
                async move {
                    let res = heb.total_psukim();
                    Json(res)
                }
            }
        }))
        .route("/psukim/:name", get({
            let heb = heb.clone();
            move |Path(name): Path<String>| {
                let heb = heb.clone();
                async move {
                    let res = heb.psukim_by_name(&name);
                    Json(res)
                }
            }
        }));

    let listener = TcpListener::bind("0.0.0.0:3000").await.unwrap();
    println!("Listening on http://{}", listener.local_addr().unwrap());
    axum::serve(listener, app)
        .await
        .unwrap();
}
