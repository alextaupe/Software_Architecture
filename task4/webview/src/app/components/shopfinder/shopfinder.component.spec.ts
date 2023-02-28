import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopfinderComponent } from './shopfinder.component';

describe('ShopfinderComponent', () => {
  let component: ShopfinderComponent;
  let fixture: ComponentFixture<ShopfinderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShopfinderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShopfinderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
