board name=BoardA

# define a ball
ball name=BallA x=10 y=15 xVelocity=0 yVelocity=50

# define a portal
portal name=portalA x=19 y=5 otherBoard=BoardB otherPortal=portalB


# define an absorber to catch the ball
 absorber name=Abs x=0 y=18 width=20 height=2

fire trigger=Abs action=Abs