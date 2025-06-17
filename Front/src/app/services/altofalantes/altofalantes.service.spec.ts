import { TestBed } from '@angular/core/testing';

import { AutofalantesService } from './altofalantes.service';

describe('AutofalantesService', () => {
  let service: AutofalantesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AutofalantesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
