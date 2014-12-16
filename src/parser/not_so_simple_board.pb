board name=SimpleBoard gravity = 20.0 friction1 = 0.001 friction2 = 0.001

# define a ball
ball name=BallC x=3 y=4 xVelocity=0 yVelocity=0
ball name=BallD x=4.4 y=3 xVelocity=0 yVelocity=0

# define some bumpers
absorber name=Abs x=10 y=17 width=10 height=2 
circleBumper name=Circle x=4 y=1
triangleBumper name=Tri x=19 y=3 orientation=90
squareBumper name=Square x=0 y=10
squareBumper name=SquareB x=1 y=10
squareBumper name=SquareC x=2 y=10
squareBumper name=SquareD x=3 y=10

# define some flippers
  leftFlipper name=FlipL x=4 y=10 orientation=270
  rightFlipper name=FlipR x=9 y=8 orientation=90

# define an absorber to catch the ball

# make the absorber self-triggering
 fire trigger=SquareD action=FlipL
 fire trigger=SquareC action=FlipL
 fire trigger=SquareB action=FlipL
 fire trigger=Square action=FlipL
fire trigger=Abs action=Abs

 
