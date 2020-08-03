function diffuse(b, x, x0, diff, dt) {
  // diffuse any abitrary array of numbers(x)
  // based on previous values(x0), diffusion amount(diff) and time step(dt)

  let a = dt * diff * (N - 2) * (N - 2);
  linearSolve(b, x, x0, a, 1 + 6 * a);
}

function linearSolve(b, x, x0, a, c) {
  let cRecip = 1.0 / c;
  for (let k = 0; k < iter; k++) {
    for (let j = 1; j < N - 1; j++) {
      for (let i = 1; i < N - 1; i++) {
        x[IX(i, j)] = (x0[IX(i, j)] + a * (x[IX(i + 1, j)] + x[IX(i - 1, j)] + x[IX(i, j - 1)])) * cRecip;
      }
    }
    setBoundary(b, x);
  }
}

function project(velocityX, velocityY, p, div) {
  /* project: causing the simulation to only of incompressible fluids
    The amount of fluid in each box must remain constant.
    (i.e: The amount of fluid going in has to be the same amount going out)
    project() runs through each cell and "fixes" them so everything is in equilibrium */

  for (let j = 1; j < N - 1; j++) {
    for (let i = 1; i < N - 1; i++) {
      div[IX(i, j)] = -0.5 * (velocityX[IX(i + 1, j)] - velocityX[IX(i - 1, j)] + velocityY[IX(i, j + 1)] - velocityY[IX(i, j - 1)]) / N;
      p[IX(i, j)] = 0;
    }
  }
  setBoundary(0, div);
  setBoundary(0, p);
  linearSolve(0, p, div, 1, 6);

  for (let j = 1; j < N - 1; j++) {
    for (let i = 1; i < N - 1; i++) {
      velocityX[IX(i, j)] -= 0.5 * (p[IX(i + 1, j)] - p[IX(i - 1, j)]) * N;
      velocityY[IX(i, j)] -= 0.5 * (p[IX(i, j + 1)] - p[IX(i, j - 1)]) * N;
    }
  }
  setBoundary(1, velocityX);
  setBoundary(2, velocityY);
}

function advect(b, d, d0, velocityX, velocityY, dt) {
  /*
   Advection: while every cell has a set of velocities, the velocity with make things move.
   Along with diffusion, advection applies to both the dye and velocities
  */

  let i0, i1, j0, j1;

  let dtx = dt * (N - 2);
  let dty = dt * (N - 2);

  let s0, s1, t0, t1;
  let tmp1, tmp2, x, y;

  let Nfloat = N;
  let ifloat, jfloat;
  let i, j;

  for (j = 1, jfloat = 1; j < N - 1; j++, jfloat++) {
    for (i = 1, ifloat = 1; i < N - 1; i++, ifloat++) {
      tmp1 = dtx * velocityX[IX(i, j)];
      tmp2 = dty * velocityY[IX(i, j)];
      x = ifloat - tmp1;
      y = jfloat - tmp2;

      if (x < 0.5 ) x = 0.5;
      if (x > Nfloat + 0.5) x = Nfloat + 0.5;
      i0 = Math.floor(x);
      i1 = i0 + 1.0;
      if (y < 0.5) y = 0.5;
      if (y > Nfloat + 0.5) y = Nfloat + 0.5;
      j0 = Math.floor(y);
      j1 = j0 + 1.0;


      s1 = x - i0;
      s0 = 1.0 - s1;
      t1 = y - j0;
      t0 = 1.0 - t1;

      let i0i = parseInt(i0);
      let i1i = parseInt(i1);
      let j0i = parseInt(j0);
      let j1i = parseInt(j1);

      d[IX(i, j)] = s0 * (t0 * d0[IX(i0i, j0i)] + t1 * d0[IX(i0i, j1i)]) + s1 * (t0 * d0[IX(i1i, j0i)] + t1 * d0[IX(i1i, j1i)]);
    }
  }
  setBoundary(b, d);
}

function setBoundary(b, x){
  //reversing the velocity in all of the edge columns/rows according to the next to column/row (mirror the velocity of the edge points)

  for(let i = 1; i < N - 1; i++) {
    x[IX(i, 0)] = b == 2 ? -x[IX(i, 1)] : x[IX(i, 1)]; //counteract the velocity from 1 row below
    x[IX(i, N-1)] = b == 2 ? -x[IX(i, N-2)] : x[IX(i, N-2)];
  }

  for(let j = 1; j < N - 1; j++) {
    x[IX(0  , j)] = b == 1 ? -x[IX(1  , j)] : x[IX(1  , j)];
    x[IX(N-1, j)] = b == 1 ? -x[IX(N-2, j)] : x[IX(N-2, j)];
  }

  //corner averages the two neighbors
  x[IX(0,0)] = 0.5 * (x[IX(1,0)] + x[IX(0,1)]);
  x[IX(0, N - 1)] = 0.5 * (x[IX(1, N - 1)] + x[IX(0, N - 2)]);
  x[IX(N - 1 ,0)] = 0.5 * (x[IX(N - 2 ,0)] + x[IX(N - 1 ,1)]);
  x[IX(N - 1, N - 1)] = 0.5 * (x[IX(N - 2, N - 1)] + x[IX(N - 1, N - 2)]);

}