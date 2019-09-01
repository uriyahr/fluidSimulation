final int N =  256;
final int  iter = 10;
int IX(int x, int y){
    // return 1-dimensional index from any given x,y
    // get 2D location in 1D array
    return x + y * N;
}
class Fluid {
    size = int size;
    float dt;       // time-step: >1
    float diff;     // diffusion amount: control vector and the "dye" diffuse out throughout the fluid
    float visc;     // viscocity: thickeness of the fluid

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

    void addDensity(int x, int y, float amount) {
        int index = IX(x,y);
        this.density[index] += amount; // add density to this location
    }

    void addVelocity(int x, int y, float amountX, float amountY) {
        int index = IX(x,y);
        this.Vx[index] += amountX; 
        this.Vy[index] += amountY;
    }
}


void diffuse(int b, float[] x, float[] x0, float diff, float dt){
    // diffuse any abitrary array of numbers(x) 
    // based on previous values(x0), diffusion amount(diff) and time step(dt)
    float a = dt * diff * (N - 2) *(N - 2);
    lin_solve(b, x, x0, a, 1 + 6 * a, iter, N);

}

void lin_solve(int b, float[] x, float[] x0, float a , float c){
    float cRecip =  1.0 / c;
    for (int k = 0; k < iter, k++){
        for (int j = 1; j < N - 1; j++){
            for (int i = 1; i < N - 1; i++){
                (x[IX(i,j)] = (x0[IX(i,j)]) + a * (x[IX(i+1, j)] + x0[IX(i - 1,j)] + x0[IX(i,j + 1)] + x0[IX(i,j - 1)] )) * cRecip;
            }
        }
    
    }
}