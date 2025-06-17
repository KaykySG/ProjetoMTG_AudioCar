import { TestBed } from '@angular/core/testing';

import { CrossoversService } from './crossovers.service';

describe('CrossoversService', () => {
  let service: CrossoversService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CrossoversService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
