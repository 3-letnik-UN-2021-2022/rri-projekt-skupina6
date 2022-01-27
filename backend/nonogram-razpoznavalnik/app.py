from flask import Flask, request
import skimage
from PIL import Image
import os
import nonogram


port = int(os.environ.get('PORT', 5000))

app = Flask(__name__)


@app.route('/')
def index():
    return "HI"


@app.route('/solve', methods=['POST'])
def image_evaluate():
    r = request

    file_str = r.files["image"]
    img = Image.open(file_str)
    img.save('output.png')

    slika_vh = skimage.io.imread('output.png')

    tiles = [
        [0, 0, 1, 0, 0],
        [0, 0, 1, 0, 0],
        [0, 1, 0, 1, 0],
        [0, 0, 1, 0, 0],
        [0, 0, 0, 0, 1],
    ]

    solved = nonogram.solve(slika_vh, tiles)

    response = ""

    for e in solved:
        for s in e:
            response += str(s)

    return response


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=port, debug=True)
