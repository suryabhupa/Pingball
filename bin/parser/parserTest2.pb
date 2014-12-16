board name=Default gravity = 25.0

# define a ball
ball name=BallA x=1.25 y=1.25 xVelocity=0 yVelocity=0
ball name=BallB x=1.0 y=1.0 xVelocity=0 yVelocity=1

# define a series of square bumpers
squareBumper name=SquareA x=0 y=17
squareBumper name=SquareB x=1 y=17


# define a series of circle bumpers
circleBumper name=CircleA x=1 y=10
circleBumper name=CircleB x=7 y=18


# define a triangle bumper
triangleBumper name=Tri x=12 y=15 orientation=180

# define as absorber
absorber name=abs x=1 y=1 width=3 height=4 orientation=90

# define a flipper
leftFlipper name=flip x=5 y=5 width=3 height=3 orientation=90