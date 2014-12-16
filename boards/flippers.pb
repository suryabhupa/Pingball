board name=Flippers gravity = 25.0

# define a ball
ball name=BallA x=0.25 y=3.25 xVelocity=0 yVelocity=0
ball name=BallB x=5.25 y=3.25 xVelocity=0 yVelocity=0 
ball name=BallC x=10.25 y=3.25 xVelocity=0 yVelocity=0 
ball name=BallD x=15.25 y=3.25 xVelocity=0 yVelocity=0 
ball name=BallE x=19.25 y=3.25 xVelocity=0 yVelocity=0 

# define some left flippers
leftFlipper name=FlipA x=0 y=8 orientation=90 
leftFlipper name=FlipB x=4 y=10 orientation=90 
leftFlipper name=FlipC x=9 y=8 orientation=90
leftFlipper name=FlipD x=15 y=8 orientation=90

# define some right flippers 
rightFlipper name=FlipE x=2 y=15 orientation=0
rightFlipper name=FlipF x=17 y=15 orientation=0

# define some circle bumpers
circleBumper name=CircleA x=5 y=18
circleBumper name=CircleB x=7 y=13
circleBumper name=CircleC x=0 y=5
circleBumper name=CircleD x=5 y=5
circleBumper name=CircleE x=10 y=5
circleBumper name=CircleF x=15 y=5

# define some triangle bumpers
triangleBumper name=TriA x=19 y=0 orientation=90
triangleBumper name=TriB x=10 y=18 orientation=180

# define an absorber
absorber name=Abs x=0 y=19 width=20 height=1 


# define events between gizmos
fire trigger=CircleC action=FlipA
fire trigger=CircleE action=FlipC
fire trigger=CircleF action=FlipD
fire trigger=Abs action=FlipE
fire trigger=Abs action=FlipF
fire trigger=Abs action=Abs

keyup key=z action =FlipA
keyup key=z action= FlipB
keyup key =z action=FlipC
keyup key= z action=FlipD
keyup key  =    z action = FlipE
keydown key=z action =FlipF
keyup key=slash action=  FlipF
keydown key = space action   =Abs

# keydown key=0 action=FlipA
# keydown key=left action=FlipB
# keydown key=minus action=FlipC
# keydown key=openbracket action=FlipD
# keydown key=semicolon action=FlipE
# keydown key=comma action=FlipF
# keyup key=p action=FlipA
# keyup key=z action=FlipA

