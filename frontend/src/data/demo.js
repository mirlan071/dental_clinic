export const DEMO_PATIENTS = [
  { id: 1, firstName: 'Азамат', lastName: 'Токтосунов', phone: '+996 700 123 456', dateOfBirth: '1990-03-15', gender: 'MALE', insurancePolicy: 'Бишкек-Мед', active: true },
  { id: 2, firstName: 'Айбек', lastName: 'Мамбетов', phone: '+996 555 789 012', dateOfBirth: '1985-07-22', gender: 'MALE', insurancePolicy: null, active: true },
  { id: 3, firstName: 'Нурзат', lastName: 'Кадырова', phone: '+996 777 345 678', dateOfBirth: '1992-11-10', gender: 'FEMALE', insurancePolicy: 'Альфа', active: true },
  { id: 4, firstName: 'Султан', lastName: 'Жумабаев', phone: '+996 700 901 234', dateOfBirth: '1978-01-05', gender: 'MALE', insurancePolicy: null, active: false },
  { id: 5, firstName: 'Бегимай', lastName: 'Асанова', phone: '+996 555 567 890', dateOfBirth: '1995-09-28', gender: 'FEMALE', insurancePolicy: 'Кыргыз-Мед', active: true },
  { id: 6, firstName: 'Канат', lastName: 'Сатыбалдиев', phone: '+996 700 234 567', dateOfBirth: '1988-06-14', gender: 'MALE', insurancePolicy: 'Бишкек-Мед', active: true },
  { id: 7, firstName: 'Айгуль', lastName: 'Токтобекова', phone: '+996 555 678 901', dateOfBirth: '1993-04-20', gender: 'FEMALE', insurancePolicy: null, active: true },
  { id: 8, firstName: 'Эрлан', lastName: 'Абдыраев', phone: '+996 777 890 123', dateOfBirth: '1982-12-03', gender: 'MALE', insurancePolicy: 'Альфа', active: true },
];

export const DEMO_DOCTORS = [
  { id: 1, firstName: 'Джон', lastName: 'Смит', specialization: 'Терапевт', licenseNumber: 'DEN-001', active: true },
  { id: 2, firstName: 'Эмили', lastName: 'Джонс', specialization: 'Ортодонт', licenseNumber: 'DEN-002', active: true },
  { id: 3, firstName: 'Азамат', lastName: 'Козлов', specialization: 'Хирург', licenseNumber: 'DEN-003', active: true },
  { id: 4, firstName: 'Ольга', lastName: 'Петрова', specialization: 'Эндодонтист', licenseNumber: 'DEN-004', active: true },
  { id: 5, firstName: 'Марат', lastName: 'Исаков', specialization: 'Имплантолог', licenseNumber: 'DEN-005', active: true },
];

export const DEMO_SERVICES = [
  { id: 1, name: 'Осмотр и консультация', description: 'Первичный осмотр, диагностика состояния полости рта', price: 500, durationMinutes: 30, category: 'Диагностика' },
  { id: 2, name: 'Рентген зубов', description: 'Панорамный и прицельный рентген-снимок', price: 800, durationMinutes: 15, category: 'Диагностика' },
  { id: 3, name: 'Лечение кариеса', description: 'Лечение кариеса с пломбированием', price: 2500, durationMinutes: 60, category: 'Лечение' },
  { id: 4, name: 'Чистка зубов', description: 'Профессиональная чистка ультразвуком + полировка', price: 3000, durationMinutes: 45, category: 'Гигиена' },
  { id: 5, name: 'Удаление зуба', description: 'Простое и сложное удаление зубов', price: 3500, durationMinutes: 30, category: 'Хирургия' },
  { id: 6, name: 'Отбел зубов', description: 'Профессиональное отбеливание LED-лампой', price: 8000, durationMinutes: 90, category: 'Эстетика' },
  { id: 7, name: 'Лечение каналов', description: 'Эндодонтическое лечение корневых каналов', price: 5000, durationMinutes: 90, category: 'Лечение' },
  { id: 8, name: 'Установка брекетов', description: 'Ортодонтическое лечение брекет-системой', price: 80000, durationMinutes: 60, category: 'Ортодонтия' },
  { id: 9, name: 'Имплантация', description: 'Установка дентального импланта', price: 65000, durationMinutes: 120, category: 'Хирургия' },
];

export const DEMO_APPOINTMENTS = [
  { id: 1, startTime: '2026-06-16T09:00:00', patientFirstName: 'Азамат', patientLastName: 'Токтосунов', doctorFirstName: 'Джон', doctorLastName: 'Смит', serviceNames: ['Осмотр', 'Рентген'], status: 'SCHEDULED' },
  { id: 2, startTime: '2026-06-16T09:30:00', patientFirstName: 'Айбек', patientLastName: 'Мамбетов', doctorFirstName: 'Эмили', doctorLastName: 'Джонс', serviceNames: ['Лечение кариеса'], status: 'IN_PROGRESS' },
  { id: 3, startTime: '2026-06-16T10:00:00', patientFirstName: 'Нурзат', patientLastName: 'Кадырова', doctorFirstName: 'Джон', doctorLastName: 'Смит', serviceNames: ['Чистка зубов'], status: 'SCHEDULED' },
  { id: 4, startTime: '2026-06-16T10:30:00', patientFirstName: 'Султан', patientLastName: 'Жумабаев', doctorFirstName: 'Азамат', doctorLastName: 'Козлов', serviceNames: ['Удаление зуба'], status: 'SCHEDULED' },
  { id: 5, startTime: '2026-06-16T11:00:00', patientFirstName: 'Бегимай', patientLastName: 'Асанова', doctorFirstName: 'Эмили', doctorLastName: 'Джонс', serviceNames: ['Отбел зубов'], status: 'SCHEDULED' },
  { id: 6, startTime: '2026-06-15T14:00:00', patientFirstName: 'Канат', patientLastName: 'Сатыбалдиев', doctorFirstName: 'Азамат', doctorLastName: 'Козлов', serviceNames: ['Имплантация'], status: 'COMPLETED' },
  { id: 7, startTime: '2026-06-15T15:30:00', patientFirstName: 'Айгуль', patientLastName: 'Токтобекова', doctorFirstName: 'Джон', doctorLastName: 'Смит', serviceNames: ['Протезирование'], status: 'NO_SHOW' },
];

export const DEMO_RECORDS = [
  { id: 1, patientFirstName: 'Азамат', patientLastName: 'Токтосунов', doctorFirstName: 'Джон', doctorLastName: 'Смит', diagnosis: 'Кариес 36 зуба', treatment: 'Пломбирование каналов', recommendations: 'Контрольный осмотр через 2 недели', createdAt: '2026-06-10T10:00:00' },
  { id: 2, patientFirstName: 'Азамат', patientLastName: 'Токтосунов', doctorFirstName: 'Эмили', doctorLastName: 'Джонс', diagnosis: 'Гингивит', treatment: 'Профессиональная чистка', recommendations: 'Ирригатор, полоскания', createdAt: '2026-05-25T14:00:00' },
  { id: 3, patientFirstName: 'Айбек', patientLastName: 'Мамбетов', doctorFirstName: 'Эмили', doctorLastName: 'Джонс', diagnosis: 'Кариес 11, 21 зубов', treatment: 'В процессе лечения', recommendations: null, createdAt: '2026-06-16T09:30:00' },
  { id: 4, patientFirstName: 'Нурзат', patientLastName: 'Кадырова', doctorFirstName: 'Джон', doctorLastName: 'Смит', diagnosis: 'Здоров', treatment: 'Чистка', recommendations: 'Профосмотр раз в 6 мес', createdAt: '2026-06-01T11:00:00' },
];

export const DEMO_INVOICES = [
  { id: 1, invoiceNumber: 'INV-001', patientFirstName: 'Азамат', patientLastName: 'Токтосунов', totalAmount: 2500, paidAmount: 2500, status: 'PAID' },
  { id: 2, invoiceNumber: 'INV-002', patientFirstName: 'Айбек', patientLastName: 'Мамбетов', totalAmount: 3300, paidAmount: 1000, status: 'PARTIALLY_PAID' },
  { id: 3, invoiceNumber: 'INV-003', patientFirstName: 'Нурзат', patientLastName: 'Кадырова', totalAmount: 3000, paidAmount: 0, status: 'UNPAID' },
  { id: 4, invoiceNumber: 'INV-004', patientFirstName: 'Султан', patientLastName: 'Жумабаев', totalAmount: 3500, paidAmount: 3500, status: 'PAID' },
  { id: 5, invoiceNumber: 'INV-005', patientFirstName: 'Бегимай', patientLastName: 'Асанова', totalAmount: 8000, paidAmount: 0, status: 'UNPAID' },
  { id: 6, invoiceNumber: 'INV-006', patientFirstName: 'Канат', patientLastName: 'Сатыбалдиев', totalAmount: 65000, paidAmount: 30000, status: 'PARTIALLY_PAID' },
];
