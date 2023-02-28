import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ShopfinderComponent } from './components/shopfinder/shopfinder.component';
import { AboutComponent } from './components/about/about.component';

const routes: Routes = [
  { path: '', component: ShopfinderComponent },
  { path: 'about', component: AboutComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
