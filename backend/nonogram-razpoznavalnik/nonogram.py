import math

import numpy as np
import scipy.ndimage as ndimage
import skimage.draw as draw
import skimage.measure as measure
# import matplotlib.pyplot as plt


def getAngle(coord1, coord2):
    angle = math.degrees(math.atan2(coord1[1] - coord2[1], coord1[0] - coord2[0]))
    if angle < 0:
        return angle + 360
    else:
        return angle


def getArea(p: np.ndarray):
    return abs(
        np.sum(p[:-1, 1] * p[1:, 0]) + p[-1, 1] * p[0, 0] - np.sum(p[1:, 1] * p[:-1, 0]) - p[0, 1] * p[-1, 0]) / 2


def solve(input_image: np.ndarray, valid_tiles):
    grid_coords = findGridCoords(input_image)
    if grid_coords == []:
        return None
    tiles = getTiles(input_image, grid_coords)
    return tiles


def findGridCoords(input_image: np.ndarray):
    image: np.ndarray = input_image[:, :, :3].mean(2)
    image = image / 255.0
    image = image > 0.5

    image = ndimage.gaussian_filter(image, 1.)
    image = ndimage.laplace(image)

    contours = measure.find_contours(image, 0.5)

    if (len(contours) == 0):
        return []

    polygons = []
    for c in contours:
        approximate = measure.approximate_polygon(c, 10)
        if len(approximate) == 5:
            approximate = np.delete(approximate, len(approximate) - 1, axis=0)
            polygons += [approximate]

    if (len(polygons) == 0):
        return []

    maxIndex = 0
    for i, p in enumerate(polygons):
        if (getArea(p) > getArea(polygons[maxIndex])):
            maxIndex = i

    polygon = polygons[maxIndex]
    p_center: np.ndarray = polygon.mean(0)

    first = -1
    for i in range(4):
        if first != -1 and getAngle(polygon[first], p_center) > getAngle(polygon[i], p_center) and getAngle(polygon[i],
                                                                                                            p_center) > 180:
            first = i
        if first == -1 and getAngle(polygon[i], p_center) > 180:
            first = i

    if first == -1:
        return []

    sequence = []
    for j in range(4):
        if first + j >= 4:
            sequence += [first + j - 4]
        else:
            sequence += [first + j]

    p_copy = np.ndarray((4, 2))
    for i in range(4):
        p_copy[i] = polygon[sequence[i]]
    polygon = p_copy

    # plt.figure()
    # plt.imshow(image)
    # plt.plot(polygon[0,1], polygon[0,0], '-xr')
    # plt.plot(polygon[1,1], polygon[1,0], '-xg')
    # plt.plot(polygon[2,1], polygon[2,0], '-xb')
    # plt.plot(polygon[3,1], polygon[3,0], '-xw')
    # plt.plot(p_center[1], p_center[0], '-xy')
    # plt.show()
    return polygon


def getTiles(input_image: np.ndarray, grid_coords: np.ndarray):
    image: np.ndarray = input_image[:, :, :3].mean(2)
    image = image / 255.0
    image = image > 0.5

    delta_x = (grid_coords[1, 1] - grid_coords[0, 1]) / 5
    delta_y = (grid_coords[3, 0] - grid_coords[0, 0]) / 5

    delta_x2 = (grid_coords[1, 0] - grid_coords[0, 0]) / 5
    delta_y2 = (grid_coords[3, 1] - grid_coords[0, 1]) / 5

    coords = []

    for i in range(5):
        row = []
        for j in range(5):
            tile_coords = [
                [grid_coords[0, 0] + delta_y * i - delta_y2 * j, grid_coords[0, 1] + delta_x * j - delta_x2 * i],
                [grid_coords[0, 0] + delta_y * i - delta_y2 * (j + 1),
                 grid_coords[0, 1] + delta_x * (j + 1) - delta_x2 * i],
                [grid_coords[0, 0] + delta_y * (i + 1) - delta_y2 * (j + 1),
                 grid_coords[0, 1] + delta_x * (j + 1) - delta_x2 * (i + 1)],
                [grid_coords[0, 0] + delta_y * (i + 1) - delta_y2 * j,
                 grid_coords[0, 1] + delta_x * j - delta_x2 * (i + 1)]
            ]
            mask = draw.polygon2mask(image.shape, tile_coords)
            mask = ~mask
            mean_value = np.ma.array(image, mask=mask).mean()
            row += [mean_value]
            # print(mean_value)
            # plt.figure()
            # plt.plot(tile_coords[0][1],tile_coords[0][0], '-ro')
            # plt.plot(tile_coords[1][1],tile_coords[1][0], '-go')
            # plt.plot(tile_coords[2][1],tile_coords[2][0], '-bo')
            # plt.plot(tile_coords[3][1],tile_coords[3][0], '-yo')
            # plt.imshow(input_image)
            # plt.show()
        coords += [row]

    for i in range(5):
        for j in range(5):
            coords[i][j] = 0 if coords[i][j] >= 0.5 else 1

    return coords
