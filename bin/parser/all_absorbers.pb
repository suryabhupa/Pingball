board name=Absorber gravity = 25.0

# define a ball
ball name=BallA x=10.25 y=2.25 xVelocity=0 yVelocity=0
ball name=BallB x=19.25 y=3.25 xVelocity=0 yVelocity=0
ball name=BallC x=1.25 y=5.25 xVelocity=0 yVelocity=0

# define an absorber to catch the ball
 absorber name=Abs x=0 y=18 width=20 height=2
 absorber name=Abs1 x=0 y=9 width=8 height=3
 absorber name=Abs2 x=9 y=4 width=5 height=4
 
# define events between gizmos
fire trigger=Abs action=Abs
fire trigger=Abs1 action=Abs1
fire trigger=Abs2 action=Abs2