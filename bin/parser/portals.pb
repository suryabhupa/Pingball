board name=Portals gravity =0.0 friction1 =0 friction2 = 0

# define a ball
ball name=BallA x=15 y=5 xVelocity=0 yVelocity=10

#define a portal
portal name=PortalA  x=15 y=15 otherBoard=Portals otherPortal=PortalB
portal name=PortalB  x=5 y=5 otherBoard=Portals otherPortal=PortalA
portal name=PortalC  x=5 y=10 otherBoard=Nowhere otherPortal=PortalZ