let N = 128;
let iter = 4;
let SCALE = 5;

function IX(x, y) {
  // return 1-dimensional index from any given x,y
  // get 2D location in 1D array
  return x + y * N;
}

class Fluid {

  constructor(dt, diffusion, viscosity) {
    this.size = N;
    this.dt = dt; // time-step: > 1
    this.diffusion = diffusion; // diffustion amount: control vector and the "dye" diffuse out throughout the fluid
    this.viscosity = viscosity; // viscosity: thickness of the fluid

    this.s = new Array(N * N).fill(0); // previous density
    this.density = new Array(N * N).fill(0);

    // velocity x, y
    this.Vx = new Array(N * N).fill(0);
    this.Vy = new Array(N * N).fill(0);

    // previous velocity x, y
    this.Vx0 = new Array(N * N).fill(0);
    this.Vy0 = new Array(N * N).fill(0);

  }

  step() {
    let N = this.size;
    let viscosity = this.viscosity;
    let diffusion = this.diffusion;
    let dt = this.dt;
    let Vx = this.Vx;
    let Vy = this.Vy;
    let Vx0 = this.Vx0;
    let Vy0 = this.Vy0;
    let s = this.s;
    let density = this.density;

    // diffuse velocities based on time step and viscosity
    diffuse(1, Vx0, Vx, viscosity, dt);
    diffuse(2, Vy0, Vy, viscosity, dt);

    // cleaning up: be sure its the same amount of fluid
    project(Vx0, Vy0, Vx, Vy);

    // advect on the velocity x and y
    advect(1, Vx, Vx0, Vx0, Vy0, dt);
    advect(2, Vy, Vy0, Vx0, Vy0, dt);

    project(Vx, Vy, Vx0, Vy0);

    // diffuse and advect the density
    diffuse(0, s, density, diffusion, dt);
    advect(0, density, s, Vx, Vy, dt);

    /* NOTE: Density doesnt need the advect step because it is consistent,
      rather than the dye which is inconsistent(needing project step) */
  }

  addDensity(x, y, amount) {
    let index = IX(x, y);
    this.density[index] += amount; // add density to this location
  }

  addVelocity(x, y, amountX, amountY) {
    let index = IX(x, y);
    this.Vx[index] += amountX;
    this.Vy[index] += amountY;
  }

  renderDensity() {
    for (let i = 0; i < N; i++) {
      for (let j = 0; j < N; j++) {
        let x = i * SCALE;
        let y = j * SCALE;
        let d = this.density[IX(i, j)];
        fill(d);
        noStroke();
        rect(x, y, SCALE, SCALE);
      }
    }
  }

  renderVelocity() {
    for (let i = 0; i < N; i++) {
      for (let j = 0; j < N; j++) {
        let x = i * SCALE;
        let y = j * SCALE;
        let vx = this.Vx[IX(i, j, k)];
        let vy = this.Vy[IX(i, j, k)];
        this.canvas.stroke(0);

        if (!(abs(vs) < 0.1 && abs(vy) <= 0.1)) {
          line(x, y, x + vx * SCALE, y + vy * SCALE);
        }
      }
    }
  }
}