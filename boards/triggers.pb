board name=testTriggers

# defining a triangle bumper
triangleBumper name=Tri x=10 y=10 orientation=180

# defining some circle bumpers
circleBumper name=Circle x=11 y=10

# defining a square bumper
squareBumper name=Square x=12 y=10

# defining a leftFlipper
leftFlipper name=FlipperL x=10 y=15

# defining a rightFlipper
rightFlipper name=FlipperR x=15 y=15

# define an absorber to catch the ball
 absorber name=Abs x=0 y=18 width=20 height=2

# define portal
portal name=aPortal x=3 y=3 otherPortal=Beta

# define random bumper
circleBumper name=last x=5 y=5
 
# define events between gizmos
fire trigger=Tri action=Circle
fire trigger=Circle action=Square
fire trigger=Square action=FlipperL
fire trigger=FlipperL action=FlipperR
fire trigger=FlipperR action=Abs
fire trigger=Abs action=aPortal
fire trigger=aPortal action=last