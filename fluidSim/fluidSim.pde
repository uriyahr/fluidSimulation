Fluid fluid;

void settings(){
    size(N*SCALE,N*SCALE);  // dimension 
}


void setup(){
                 
    fluid = new Fluid(0.1 , 0 , 0);   // Fluid(time-step, diffusion, viscocity)
}
void mouseDragged(){
  fluid.addDensity(mouseX/SCALE,mouseY/SCALE,100);
  float amountX = mouseX - pmouseX;
  float amountY = mouseY- pmouseY;
  fluid.addVelocity(mouseX/SCALE,mouseY/SCALE,amountX,amountY);

}

void draw(){
  background(200);
  fluid.step();
  fluid.renderD();
  fluid.fadeD();
}
