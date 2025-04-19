import { ShiftStatus } from './shiftStatus';

export class Shift {
  shiftId!: number;
  employeeId!: number;
  shiftDate!: string;
  shiftTime!: string;
  shiftStatus!: ShiftStatus;
}
