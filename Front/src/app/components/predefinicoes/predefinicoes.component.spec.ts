import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PredefinicoesComponent } from '././predefinicoes.component';

describe('PreinfinicoesComponent', () => {
  let component: PredefinicoesComponent;
  let fixture: ComponentFixture<PredefinicoesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PredefinicoesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PredefinicoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
