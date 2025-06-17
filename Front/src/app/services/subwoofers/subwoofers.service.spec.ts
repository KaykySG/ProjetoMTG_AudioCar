import { TestBed } from '@angular/core/testing';

import { SubwoofersService } from './subwoofers.service';

describe('SubwoofersService', () => {
  let service: SubwoofersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubwoofersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
