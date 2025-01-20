from flask import Flask, request, jsonify
from heb_bible import HebBible

app = Flask(__name__)
heb_bible = HebBible()

@app.route('/psukim', methods=['GET'])
def total_psukim():
    result = heb_bible.total_psukim()
    return jsonify(result)

@app.route('/psukim/<string:name>')
def psukim_by_name(name):
    result = heb_bible.psukim_by_name(name)
    return jsonify(result)

if __name__ == '__main__':
    app.run(host = '0.0.0.0', port = 9000, debug = False)
