import nonogram
import matplotlib.pyplot as plt
import numpy as np
import skimage.io

tiles = [
    [0, 0, 1, 0, 0],
    [0, 0, 1, 0, 0],
    [0, 1, 0, 1, 0],
    [0, 0, 1, 0, 0],
    [0, 0, 0, 0, 1],
]

slika_vh = skimage.io.imread('./grid.png')
solved = nonogram.solve(slika_vh, tiles)

print(solved)
