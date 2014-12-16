board name=oldAbsorber gravity = 25.0

# define a ball
ball name=BallA x=10.25 y=15.25 xVelocity=0.0 yVelocity=0.1
ball name=BallB x=19.25 y=3.25 xVelocity=0.1 yVelocity=0.1
ball name=BallC x=1.25 y=5.25 xVelocity=0.1 yVelocity=0.1

# defining a triangle bumper
triangleBumper name=Tri x=19 y=0 orientation=180

# defining some circle bumpers
circleBumper name=CircleA x=1 y=10
circleBumper name=CircleB x=2 y=10
circleBumper name=CircleC x=3 y=10
circleBumper name=CircleD x=4 y=10
circleBumper name=CircleE x=5 y=10

# define an absorber to catch the ball
 absorber name=Abs x=0 y=18 width=20 height=2
 
# define events between gizmos
fire trigger=CircleA action=Abs
fire trigger=CircleB action=Abs
fire trigger=CircleC action=Abs
fire trigger=CircleD action=Abs
fire trigger=CircleE action=Abs