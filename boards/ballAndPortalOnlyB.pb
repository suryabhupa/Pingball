board name=BoardB

# define a ball
ball name=BallB x=10 y=15 xVelocity=0 yVelocity=50

# define a portal
portal name=portalB x=19 y=12 otherBoard=BoardA otherPortal=portalA

# define an absorber to catch the ball
 absorber name=Abs x=0 y=18 width=20 height=2

fire trigger=Abs action=Abs