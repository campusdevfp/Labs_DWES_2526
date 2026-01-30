import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

export const appConfig = {
  providers: [provideHttpClient(withInterceptorsFromDi())],
};
