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
    linearSolve(b, x, x0, a, 1 + 6 * a, iter, N);

}

void linearSolve(int b, float[] x, float[] x0, float a , float c){
    float cRecip =  1.0 / c;
    for (int k = 0; k < iter, k++){
        for (int j = 1; j < N - 1; j++){
            for (int i = 1; i < N - 1; i++){
                (x[IX(i,j)] = (x0[IX(i,j)] + a * (x[IX(i-1, j)] + x[IX(i,j + 1)] + x[IX(i,j - 1)] )) * cRecip;
            }
        }
    }
}

void project(float[] velocX, float[] velocY, float[] p, float[] div){
    /* project: causing the simulation to only of incompressible fluids
       The amount of fluid in each box must remain constant.
       (i.e: The amount of fluid going in has to be the same amount going out)
       project() runs through each cell and "fixes" them so everything is in equilibrium */
        
    for(int j = 1, j < N - 1, j++){
        for(int i = 1; i < N -1; i++){
            div[IX(i,j)] = -0.5f * (velocX[IX(i+1,j)] - velocX[IX(i-1,j)] + velocY[IX(i, j+1)] - velocY[IX(i, j-1)]) / N;
            p[IX((i,j))] = 0;
        }
    }
    setBoundary(0,div,N);
    setBoundary(0,p,N);
    linearSolve(0,p,div,1,6);

    for(int j = 1; j < N - 1; j++){
        for(int i = 1; i < N -1; i++){
            velocX[IX(i,j)] -= 0.5f * (p[IX(i+1,j)] - p[IX(i-1,j)]) * N;
            velocY[IX(i,j)] -= 0.5f * (p[IX(i, j+1)] - p[IX(i, j-1)]) * N;
        }
    }
    
    setBoundary(1, velocX, N);
    setBoundary(2, velocY, N);

}

void advect(int b, float[] d, float[] d0,  float[] velocX, float[] velocY, float dt, int N){
    float i0, i1, j0, j1;
    
    float dtx = dt * (N - 2);
    float dty = dt * (N - 2);
    
    float s0, s1, t0, t1;
    float tmp1, tmp2, x, y;
    
    float Nfloat = N;
    float ifloat, jfloat, kfloat;
    int i, j;
    
    for(j = 1, jfloat = 1; j < N - 1; j++, jfloat++) { 
        for(i = 1, ifloat = 1; i < N - 1; i++, ifloat++) {
            tmp1 = dtx * velocX[IX(i, j, k)];
            tmp2 = dty * velocY[IX(i, j, k)];
            x    = ifloat - tmp1; 
            y    = jfloat - tmp2;
            
            if(x < 0.5f) x = 0.5f; 
            if(x > Nfloat + 0.5f) x = Nfloat + 0.5f; 
            i0 = floor(x); 
            i1 = i0 + 1.0f;
            if(y < 0.5f) y = 0.5f; 
            if(y > Nfloat + 0.5f) y = Nfloat + 0.5f; 
            j0 = floor(y);
            j1 = j0 + 1.0f; 

            
            s1 = x - i0; 
            s0 = 1.0f - s1; 
            t1 = y - j0; 
            t0 = 1.0f - t1;
           
            int i0i = int(i0);
            int i1i = int(i1);
            int j0i = int(j0);
            int j1i = int(j1);
            // DOUBLE CHECK THIS!!! ;/
            d[IX(i, j)] =  s0 * ( t0 * d0[IX(i0i, j0i)]) + (t1 + d0[IX((i0i, j1i))]) + s1 * (t0 * d0[IX(i1i,j0i)]) + (t1 * d0[IX(i1i, j1i)])
                         
        } 
    }
    
    setBoundary(b, d, N);
}
void setBoundary(int b, float *x, int N){

    //REFACTOR
    for(int k = 1; k < N - 1; k++) {
        for(int i = 1; i < N - 1; i++) {
            x[IX(i, 0)] = b == 2 ? -x[IX(i, 1)] : x[IX(i, 1)];
            x[IX(i, N-1)] = b == 2 ? -x[IX(i, N-2)] : x[IX(i, N-2)];
        }
    }
    for(int k = 1; k < N - 1; k++) {
        for(int j = 1; j < N - 1; j++) {
            x[IX(0  , j, k)] = b == 1 ? -x[IX(1  , j, k)] : x[IX(1  , j, k)];
            x[IX(N-1, j, k)] = b == 1 ? -x[IX(N-2, j, k)] : x[IX(N-2, j, k)];
        }
    }
    
    x[IX(0, 0, 0)]       = 0.33f * (x[IX(1, 0, 0)]
                                  + x[IX(0, 1, 0)]
                                  + x[IX(0, 0, 1)]);
    x[IX(0, N-1, 0)]     = 0.33f * (x[IX(1, N-1, 0)]
                                  + x[IX(0, N-2, 0)]
                                  + x[IX(0, N-1, 1)]);
    x[IX(0, 0, N-1)]     = 0.33f * (x[IX(1, 0, N-1)]
                                  + x[IX(0, 1, N-1)]
                                  + x[IX(0, 0, N)]);
    x[IX(0, N-1, N-1)]   = 0.33f * (x[IX(1, N-1, N-1)]
                                  + x[IX(0, N-2, N-1)]
                                  + x[IX(0, N-1, N-2)]);
    x[IX(N-1, 0, 0)]     = 0.33f * (x[IX(N-2, 0, 0)]
                                  + x[IX(N-1, 1, 0)]
                                  + x[IX(N-1, 0, 1)]);
    x[IX(N-1, N-1, 0)]   = 0.33f * (x[IX(N-2, N-1, 0)]
                                  + x[IX(N-1, N-2, 0)]
                                  + x[IX(N-1, N-1, 1)]);
    x[IX(N-1, 0, N-1)]   = 0.33f * (x[IX(N-2, 0, N-1)]
                                  + x[IX(N-1, 1, N-1)]
                                  + x[IX(N-1, 0, N-2)]);
    x[IX(N-1, N-1, N-1)] = 0.33f * (x[IX(N-2, N-1, N-1)]
                                  + x[IX(N-1, N-2, N-1)]
                                  + x[IX(N-1, N-1, N-2)]);
}
