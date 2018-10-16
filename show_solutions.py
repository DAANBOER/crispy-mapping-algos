from zipfile import ZipFile
import matplotlib.pyplot as plt
import sys


def problems_for_file(infilename, outfilename):
    zfo = ZipFile(outfilename)
    zfi = ZipFile(infilename)
    problems = []

    for p_name in zfo.namelist():
        with zfi.open(p_name) as input_f:
            input_lines = list(map(lambda l: list(map(float, l.split(" "))), filter(lambda s: s.strip(), input_f.read().decode().split('\n')[3:])))

        with zfo.open(p_name) as output_f:
            output_lines = list(map(lambda l: list(map(float, l.split(" "))), filter(lambda s: s.strip(), output_f.read().decode().split('\n')[2:])))

        problems.append((p_name, input_lines, output_lines))
    return problems


def print_one_square(center, size=2):
    x, y = center
    coord = [[x+size/2,y+size/2], [x+size/2,y-size/2], [x-size/2,y-size/2], [x-size/2,y+size/2]]
    coord.append(coord[0]) #repeat the first point to create a 'closed loop'

    xs, ys = zip(*coord) #create lists of x and y values
    plt.plot(xs,ys)


def print_one_dot(center, size):
    fig = plt.gcf()
    ax = fig.gca()
    circle = plt.Circle(center, radius=0.3*size/2, alpha=0.5)
    ax.add_artist(circle)

def connect_dot_to_circle(center_dot, center_circle):
    coord = [center_dot, center_circle]
    xs, ys = zip(*coord) #create lists of x and y values
    plt.plot(xs,ys, color='black', alpha=0.5)


def print_problem(p):
    name, input_lines, output_lines = p
    f = plt.figure()
    f.suptitle(name)

    for (dot_x, dot_y, size), out_center in zip(input_lines, output_lines):
        in_center = (dot_x, dot_y)
        print_one_square(out_center, size)
        print_one_dot(in_center, size)
        connect_dot_to_circle(in_center, out_center)

    plt.axis('equal')
    plt.show()


if len(sys.argv) < 3:
    print("Not enough arguments. Needs inputzip and outputzip")
    exit(1)

problems = problems_for_file(infilename=sys.argv[1], outfilename=sys.argv[2])
for p in problems:
    print_problem(p)