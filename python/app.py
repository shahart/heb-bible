from fastapi import FastAPI
from fastapi.responses import JSONResponse
from heb_bible import HebBible
import uvicorn

app = FastAPI()
heb_bible = HebBible()

@app.get('/psukim')
def total_psukim():
    result = heb_bible.total_psukim()
    return JSONResponse(content=result)

@app.get('/psukim/{name}')
def psukim_by_name(name):
    result = heb_bible.psukim_by_name(name)
    return JSONResponse(content=result)

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=9000)
