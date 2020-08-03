final int N =  128;
final int iter = 4;
final int SCALE = 5;

int IX(int x, int y){
    // return 1-dimensional index from any given x,y
    // get 2D location in 1D array
    x = constrain(x, 0, N-1);
    y = constrain(y, 0, N-1);
    return x + (y * N);
}
class Fluid {
    int size;
    float dt;       // time-step: >1
    float diff;     // diffusion amount: control vector and the "dye" diffuse out throughout the fluid
    float visc;     // viscosity: thickeness of the fluid

    float [] s;     //previous density
    float [] density;

    // velocity x,y
    float [] Vx;
    float [] Vy;

    // previous velocity x,y
    float[] Vx0;
    float[] Vy0;

    // constructor of fluiiiIIiid
    Fluid(float dt, float diffusion, float viscocity) {
        this.size = N;
        this.dt = dt;
        this.diff = diffusion;
        this.visc = viscocity;

        this.s = new float[N*N];
        this.density = new float[N*N];

        this.Vx = new float[N*N];
        this.Vy = new float[N*N];

        this.Vx0 = new float[N*N];
        this.Vy0 = new float[N*N];
    }
    void step(){

      float visc     = this.visc;
      float diff     = this.diff;
      float dt       = this.dt;
      float[] Vx      = this.Vx;
      float[] Vy      = this.Vy;
      float[] Vx0     = this.Vx0;
      float[] Vy0     = this.Vy0;
      float[] s       = this.s;
      float[] density = this.density;

      // diffuse velocities based on time step and viscosity
      diffuse(1, Vx0, Vx, visc, dt);
      diffuse(2, Vy0, Vy, visc, dt);

      // clean everything up;  making sure its the
      // same amount of fluid
       project(Vx0, Vy0, Vx, Vy);

      // advect on the velocity x and y
      advect(1,Vx, Vx0, Vx0, Vy0, dt);
      advect(2, Vy, Vy0, Vx0, Vy0, dt);

      project(Vx, Vy, Vx0, Vy0);

      // diffuse and advect the density
      diffuse(0, s, density, diff, dt);
      advect(0, density, s, Vx, Vy, dt);

      /* NOTE: Density doesnt need the advect step because it is consistent,
      rather than the dye which is inconsistent(needing project step) */
    }
    void addDensity(int x, int y, float amount) {
        int index = IX(x,y);
        this.density[index] += amount; // add density to this location
    }

    void addVelocity(int x, int y, float amountX, float amountY) {
        int index = IX(x,y);
        this.Vx[index] += amountX;
        this.Vy[index] += amountY;
    }
    void renderD(){
      colorMode(HSB,255);
      for(int i = 0; i < N; i++){
        for (int j =0; j < N; j++){
          float x = i * SCALE;
          float y = j * SCALE;
          float d = this.density[IX(i,j)];
          fill((d + 90) % 50,200,d);
          noStroke();
          square(x,y,SCALE);
        }
      }
    }
    // checklist: faceDye, render veloc

    void fadeD(){
      for(int i = 0; i < this.density.length; i++){
        float d = density[i];
        density[i] = constrain(d-0.08, 0,255);


      }
    }
}
